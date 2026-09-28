package com.alpha.rider.requestdto;

public class VehicleDto {
	private String type;
	private String vehicleno;
	private String vname;
	private String model;
	private String status;
	public VehicleDto(String type, String vehicleno, String vname, String model, String status) {
		super();
		this.type = type;
		this.vehicleno = vehicleno;
		this.vname = vname;
		this.model = model;
		this.status = status;
	}
	public VehicleDto() {
		super();
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVehicleno() {
		return vehicleno;
	}
	public void setVehicleno(String vehicleno) {
		this.vehicleno = vehicleno;
	}
	public String getVname() {
		return vname;
	}
	public void setVname(String vname) {
		this.vname = vname;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	

}
