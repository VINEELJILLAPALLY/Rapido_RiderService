package com.alpha.rider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PatchExchange;

import com.alpha.rider.entity.LocationCoordinates;
import com.alpha.rider.entity.Rider;
import com.alpha.rider.requestdto.RiderCreateDto;
import com.alpha.rider.requestdto.VehicleUpdateDto;
import com.alpha.rider.responsedto.ResponseStructure;
import com.alpha.rider.service.RiderService;

@RestController
public class RiderController {
	@Autowired
	private RiderService riderservice;
	
	@PostMapping("/rider/createaccount")
	public ResponseStructure<RiderCreateDto> createRiderAccount(@RequestBody RiderCreateDto ridercreatedto) {
		 return riderservice.createRiderAccount(ridercreatedto);
	}
	
	@PatchMapping("/rider/{id}/updatevehicle")
	public ResponseStructure<Rider> updateVehicle(@PathVariable int id,@RequestBody VehicleUpdateDto vehicleupdatedto) {
		 return riderservice.updateVehicle(id,vehicleupdatedto);
	}
	
	@PatchMapping("/rider/{id}/{newstatus}/updatestatus")
	public ResponseStructure<Rider> updateStatus(@PathVariable int id,@PathVariable String newstatus) {
		 return riderservice.updateStatus(id,newstatus);
	}
	@PostMapping("/rider/sharecurrentlocation")
	public void sendCurrentLoc(@RequestParam String vehicletype,int id,@RequestBody LocationCoordinates coordinates) {
		riderservice.sendCurrentLoc(vehicletype,id,coordinates);
	}

}
