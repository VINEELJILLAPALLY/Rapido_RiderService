package com.alpha.rider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
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

@Service
public class RiderService {
	@Autowired
	private RiderRepository riderrepository;

	@Autowired
	private RedisTemplate<String, Object> redistemplate;
	@Autowired
	private RestTemplate resttemplate;

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

		System.out.println("Redis connection factory = " + redistemplate.getConnectionFactory());
		Long result = redistemplate.opsForGeo().add(key,
				new Point(coordinates.getLongitude(), coordinates.getLatitude()), String.valueOf(id));
		System.out.println("GEO ADD RESULT = " + result);
		System.out.println("GEO KEY SIZE = " + redistemplate.opsForZSet().size(key));

		System.out.println("Redis DB size = " + redistemplate.getConnectionFactory().getConnection().dbSize());

	}

}
