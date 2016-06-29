package com.example.chetna_priya.myapplication.backend.form;




/**
 * Created by chetna_priya on 1/25/2016.
 */


import com.google.appengine.api.images.Image;

/**
 * A simple Java object (POJO) representing a registration form sent from the client.
 */
public class DriverRegForm
{

    private String firstName;

    private String lastName;

    private String email;

    private String address;

    private String password;

    private String phoneNo;

    private Image driverImage;

    private DriverRegForm()
    {}

    public DriverRegForm(String firstName, String lastName, String email, String address, String password, String phoneNo, Image driverImage)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.password = password;
        this.phoneNo = phoneNo;
        this.driverImage = driverImage;
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
