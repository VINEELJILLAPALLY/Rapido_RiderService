package com.alpha.rider.requestdto;

import com.alpha.rider.entity.Vehicle;

import jakarta.persistence.OneToOne;

public class RiderCreateDto {
	private String name;
	private long mobno;
	private String gender;
	private String email;
	private String drivinglicense;
	
	@OneToOne
	private VehicleDto vehicledto;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getMobno() {
		return mobno;
	}

	public void setMobno(long mobno) {
		this.mobno = mobno;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDrivinglicense() {
		return drivinglicense;
	}

	public void setDrivinglicense(String drivinglicense) {
		this.drivinglicense = drivinglicense;
	}

	public VehicleDto getVehicleDto() {
		return vehicledto;
	}

	public void setVehicleDto(VehicleDto vehicledto) {
		this.vehicledto = vehicledto;
	}

	public RiderCreateDto() {
		super();
	}

	public RiderCreateDto(String name, long mobno, String gender, String email, String drivinglicense,
			VehicleDto vehicledto) {
		super();
		this.name = name;
		this.mobno = mobno;
		this.gender = gender;
		this.email = email;
		this.drivinglicense = drivinglicense;
		this.vehicledto = vehicledto;
	}
}
