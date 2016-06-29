package com.example.chetna_priya.myapplication.backend.domain;

import com.google.appengine.api.images.Image;

import com.example.chetna_priya.myapplication.backend.form.DriverRegForm;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by chetna_priya on 1/25/2016.
 */
@Entity
@Cache
public class Driver {

    @Id
    private long id;

    @Index
    private String firstName;

    private String lastName;

    @Index
    private String email;

    private String address;

    private String password;

    private String phoneNo;

    private Image driverImage;

    private Driver()
    {}

    public Driver(final long id, DriverRegForm driverRegForm)
    {
        this.id = id;
        updateWithDriverRegForm(driverRegForm);
    }

    private void updateWithDriverRegForm(DriverRegForm driverRegForm) {

        this.firstName = driverRegForm.getFirstName();
        this.lastName = driverRegForm.getLastName();
        this.email = driverRegForm.getEmail();
        this.phoneNo = driverRegForm.getPhoneNo();
        this.address = driverRegForm.getAddress();
        this.password = driverRegForm.getPassword();
        this.driverImage = driverRegForm.getDriverImage();
    }


    public long getId() {
        return id;
    }

    public Image getDriverImage() {
        return driverImage;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }


}
