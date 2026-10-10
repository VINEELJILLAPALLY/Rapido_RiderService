package com.alpha.rider.service;

import java.io.StringReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alpha.rider.entity.LocationCoordinates;
import com.alpha.rider.entity.Rider;
import com.alpha.rider.entity.Vehicle;
import com.alpha.rider.exception.RiderNotFoundException;
import com.alpha.rider.repository.RiderRepository;
import com.alpha.rider.requestdto.RiderCreateDto;
import com.alpha.rider.requestdto.VehicleUpdateDto;
import com.alpha.rider.responsedto.ResponseStructure;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RiderService {
	@Autowired
	private RiderRepository riderrepository;

	@Autowired
	private RedisTemplate<String, Object> redistemplate;
	@Autowired
	private RestTemplate resttemplate;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private ObjectMapper objectMapper;

	private static final DefaultRedisScript<Long> RELEASE_LOCK_SCRIPT = new DefaultRedisScript<>(
			"if redis.call('get', KEYS[1]) == ARGV[1] " + "then " + "return redis.call('del', KEYS[1]) " + "else "
					+ "return 0 " + "end",
			Long.class);

	public ResponseStructure<RiderCreateDto> createRiderAccount(RiderCreateDto ridercreatedto) {
		Rider rider = new Rider();
		Vehicle vehicle = new Vehicle();
		ResponseStructure<RiderCreateDto> responsestructure = new ResponseStructure<RiderCreateDto>();
		rider.setName(ridercreatedto.getName());
		rider.setMobno(ridercreatedto.getMobno());
		rider.setGender(ridercreatedto.getGender());
		rider.setDrivinglicense(ridercreatedto.getDrivinglicense());
		vehicle.setType(ridercreatedto.getVehicleDto().getType());
		vehicle.setVname(ridercreatedto.getVehicleDto().getVname());
		vehicle.setModel(ridercreatedto.getVehicleDto().getModel());
		vehicle.setStatus(ridercreatedto.getVehicleDto().getStatus());
		vehicle.setVehicleno(ridercreatedto.getVehicleDto().getVehicleno());
		rider.setVehicle(vehicle);
		riderrepository.save(rider);
		responsestructure.setStatuscode(HttpStatus.CREATED.value());
		responsestructure.setMessage("RIDER ACCOUNT SUCCESSFULLY CREATED");
		responsestructure.setData(ridercreatedto);
		return responsestructure;
	}

	public ResponseStructure<Rider> updateVehicle(int id, VehicleUpdateDto vehicleupdatedto) {
		Rider rider = riderrepository.findById(id).orElseThrow(() -> new RiderNotFoundException());
		rider.getVehicle().setType(vehicleupdatedto.getType());
		rider.getVehicle().setVname(vehicleupdatedto.getVname());
		rider.getVehicle().setModel(vehicleupdatedto.getModel());
		rider.getVehicle().setVehicleno(vehicleupdatedto.getVehicleno());
		rider.getVehicle().setStatus(vehicleupdatedto.getStatus());
		riderrepository.save(rider);

		ResponseStructure<Rider> responsestructure = new ResponseStructure<Rider>();
		responsestructure.setStatuscode(HttpStatus.OK.value());
		responsestructure.setMessage("VEHICLE DETAILS WITH RIDER ID" + id + "UPDATED SUCCESSFULLY");
		responsestructure.setData(rider);
		return responsestructure;
	}

	public ResponseStructure<Rider> updateStatus(int id, String newstatus) {
		Rider rider = riderrepository.findById(id).orElseThrow(() -> new RiderNotFoundException());
		rider.setStatus(newstatus);
		riderrepository.save(rider);
		ResponseStructure<Rider> responseStructure = new ResponseStructure<Rider>();
		responseStructure.setStatuscode(HttpStatus.OK.value());
		responseStructure.setMessage("RIDER STATUS WITH ID:" + id + "UPDATED SUCCESSFULLY");
		responseStructure.setData(rider);
		return responseStructure;
	}

	public void sendCurrentLoc(String vehicletype, int id, LocationCoordinates coordinates) {

		String key = "RIDER LOCATION:" + vehicletype;

		System.out.println("Redis key: " + key);
		System.out.println("Rider ID: " + id);
		System.out.println("Latitude: " + coordinates.getLatitude());
		System.out.println("Longitude: " + coordinates.getLongitude());

		Long result = stringRedisTemplate.opsForGeo().add(key,
				new Point(coordinates.getLongitude(), coordinates.getLatitude()), String.valueOf(id));

		System.out.println("GEO ADD RESULT = " + result);
	}

	public ResponseStructure<List<Map<String, Object>>> getAllAssignedRides(int rid) {

		String key = "RIDER REQUESTS:" + rid;

		Map<Object, Object> redisData = stringRedisTemplate.opsForHash().entries(key);

		List<Map<String, Object>> assignedRides = new ArrayList<>();

		for (Map.Entry<Object, Object> entry : redisData.entrySet()) {

			try {

				Map<String, Object> ride = objectMapper.readValue(entry.getValue().toString(),
						new TypeReference<Map<String, Object>>() {
						});

				assignedRides.add(ride);

			} catch (Exception e) {

				throw new RuntimeException("Unable to read assigned ride from Redis", e);
			}
		}

		return new ResponseStructure<>(HttpStatus.OK.value(), "ASSIGNED RIDES FETCHED SUCCESSFULLY", assignedRides);
	}

	public ResponseStructure<String> acceptRide(int bookingId, int riderId) {

		System.out.println("RIDER ID RECEIVED = " + riderId);
		System.out.println("BOOKING ID RECEIVED = " + bookingId);

		// 1. CHECK RIDER
		Rider rider = riderrepository.findById(riderId).orElseThrow(() -> new RiderNotFoundException());

		System.out.println("RIDER FOUND = " + rider.getId());

		// 2. CHECK WHETHER BOOKING WAS SENT TO THIS RIDER
		String riderRequestKey = "RIDER REQUESTS:" + riderId;

		Boolean requestExists = stringRedisTemplate.opsForHash().hasKey(riderRequestKey, String.valueOf(bookingId));

		if (!Boolean.TRUE.equals(requestExists)) {

			ResponseStructure<String> response = new ResponseStructure<>();

			response.setStatuscode(HttpStatus.NOT_FOUND.value());

			response.setMessage("BOOKING REQUEST NOT FOUND");

			response.setData("Booking " + bookingId + " was not sent to rider " + riderId);

			return response;
		}

		// 3. CREATE REDIS LOCK
		String lockKey = "BOOKING LOCK:" + bookingId;

		String lockValue = String.valueOf(riderId);

		Boolean lockAcquired = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockValue,
				Duration.ofSeconds(30));

		System.out.println("BOOKING LOCK ACQUIRED = " + lockAcquired);

		// 4. SOME OTHER RIDER ALREADY GOT THE LOCK
		if (!Boolean.TRUE.equals(lockAcquired)) {

			ResponseStructure<String> response = new ResponseStructure<>();

			response.setStatuscode(HttpStatus.CONFLICT.value());

			response.setMessage("BOOKING ALREADY ACCEPTED");

			response.setData("Booking " + bookingId + " is already being accepted by another rider");

			return response;
		}

		try {

			// 5. CALL CUSTOMER SERVICE
			String url = "http://localhost:8082/customer/" + bookingId + "/assignrider/" + riderId;

			ResponseStructure customerResponse = resttemplate.patchForObject(url, null, ResponseStructure.class);

			System.out.println("CUSTOMER SERVICE RESPONSE = " + customerResponse);

			// 6. SUCCESS RESPONSE
			ResponseStructure<String> response = new ResponseStructure<>();

			response.setStatuscode(HttpStatus.OK.value());

			response.setMessage("RIDE ACCEPTED");

			response.setData("Rider " + riderId + " accepted booking " + bookingId);

			return response;

		} finally {

			// 7. RELEASE ONLY THIS RIDER'S LOCK
			stringRedisTemplate.execute(RELEASE_LOCK_SCRIPT, Collections.singletonList(lockKey), lockValue);

			System.out.println("BOOKING LOCK RELEASED = " + bookingId);
		}
	}

	public ResponseStructure<Map<String,Object>> moveTowardsCustomerLoc(int bid) {
		String key="BOOKING:"+bid;
		
		Map<Object,Object> booking=stringRedisTemplate.opsForHash().entries(key);
		if(booking.isEmpty()) {
			return new ResponseStructure<>(HttpStatus.NOT_FOUND.value(),"BOOKING NOT FOUND",null);
			
		}
		Map<String,Object> response=new LinkedHashMap<>();
		
		response.put("bookingId", bid);
		response.put("pickupLocation",booking.get("pickupLocation"));
		response.put("sourceLatitude", booking.get("sourceLatitude"));
	    response.put("sourceLongitude", booking.get("sourceLongitude"));
	    
	    return new ResponseStructure<>(HttpStatus.OK.value(),"PICKUP LOCATION FETCHED SUCCESSFULLY",response);
	}

	public ResponseStructure<String> updateArrival(int bid) {
		String key="BOOKING:"+bid;
		Boolean exists=stringRedisTemplate.hasKey(key);
		if(!Boolean.TRUE.equals(exists)) {
			return new ResponseStructure<>(HttpStatus.NOT_FOUND.value(),"BOOKING NOT FOUND","Booking"+bid+"not found");
		}
		stringRedisTemplate.opsForHash().put(key, "status", "RIDER ARRIVED");
		
		return new ResponseStructure<>(HttpStatus.OK.value(),"RIDER ARRIVED AT PICKUP LOCATION","Booking"+bid+"status updated to RIDER ARRIVED");
	}

	public ResponseStructure<String> verifyOtp(int bid, String otp) {

		String key = "BOOKING:" + bid;

		Map<Object, Object> booking = stringRedisTemplate.opsForHash().entries(key);

		if (booking.isEmpty()) {
			return new ResponseStructure<>(HttpStatus.NOT_FOUND.value(), "BOOKING NOT FOUND",
					"Booking " + bid + " not found");
		}

		String customerId = booking.get("customerId").toString();

		String url = "http://localhost:8082/customer/" + customerId + "/find";

		ResponseStructure customerResponse = resttemplate.getForObject(url, ResponseStructure.class);

		Map<String, Object> customerData = (Map<String, Object>) customerResponse.getData();

		String customerOtp = customerData.get("otp").toString();

		if (!customerOtp.equals(otp)) {
			return new ResponseStructure<>(HttpStatus.BAD_REQUEST.value(), "INVALID OTP", "OTP does not match");
		}

		stringRedisTemplate.opsForHash().put(key, "status", "RIDE STARTED");

		return new ResponseStructure<>(HttpStatus.OK.value(), "OTP VERIFIED SUCCESSFULLY",
				"Ride started for booking " + bid);	
	}

	public ResponseStructure<Map<String, Object>> moveTowardsDestination(int bid) {

		String key = "BOOKING:" + bid;

		Map<Object, Object> booking = stringRedisTemplate.opsForHash().entries(key);

		if (booking.isEmpty()) {
			return new ResponseStructure<>(HttpStatus.NOT_FOUND.value(), "BOOKING NOT FOUND", null);
		}

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("bookingId", bid);
		response.put("destinationLocation", booking.get("destinationLocation"));
		response.put("destinationLatitude", booking.get("destinationLatitude"));
		response.put("destinationLongitude", booking.get("destinationLongitude"));

		return new ResponseStructure<>(HttpStatus.OK.value(), "DESTINATION LOCATION FETCHED SUCCESSFULLY", response);
	}

}
