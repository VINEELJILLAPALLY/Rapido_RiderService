package com.alpha.rider.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Rider {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String name;
	private long mobno;
	private String email;
	private String gender;
	private double wallet;
	private int noofrides;
	private String status;
	private String drivinglicense; 
	@OneToOne(cascade=CascadeType.ALL)
	private Vehicle vehicle;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public double getWallet() {
		return wallet;
	}
	public void setWallet(double wallet) {
		this.wallet = wallet;
	}
	public int getNoofrides() {
		return noofrides;
	}
	public void setNoofrides(int noofrides) {
		this.noofrides = noofrides;
	}
	public String getDrivinglicense() {
		return drivinglicense;
	}
	public void setDrivinglicense(String drivinglicense) {
		this.drivinglicense = drivinglicense;
	}
	public Vehicle getVehicle() {
		return vehicle;
	}
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	public Rider() {
		super();
	}
	public Rider(String name, long mobno, String email, String gender, double wallet, int noofrides,String status,
			String drivinglicense, Vehicle vehicle) {
		super();
		this.name = name;
		this.mobno = mobno;
		this.email = email;
		this.gender = gender;
		this.wallet = wallet;
		this.noofrides = noofrides;
		this.status=status;
		this.drivinglicense = drivinglicense;
		this.vehicle = vehicle;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
