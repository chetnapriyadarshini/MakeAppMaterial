package com.example.chetna_priya.myapplication.backend;

import com.example.chetna_priya.myapplication.backend.domain.Driver;
import com.example.chetna_priya.myapplication.backend.form.DriverRegForm;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;

import java.util.logging.Logger;

import javax.inject.Named;

import static com.google.api.server.spi.config.ApiMethod.HttpMethod.POST;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "driverApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.chetna_priya.example.com",
                ownerName = "backend.myapplication.chetna_priya.example.com",
                packagePath = ""
        )
)
public class DriverApiEndpoint {

    private static final Logger logger = Logger.getLogger(DriverApiEndpoint.class.getName());

    @ApiMethod(name = "saveProfile", path = "saveProfile", httpMethod = POST)
    public void saveProfile(@Named("firstName") String firstName,
                            @Named("lastName") String lastName,
                            @Named("email") String email,
                            @Named("address") String address,
                            @Named("password") String password,
                            @Named("phoneNo") String phoneNo)
    {
        final Key<Driver> driverKey = OfyService.factory().allocateId(Driver.class);
        final DriverRegForm driverRegForm =  new DriverRegForm(firstName,lastName,email,address,password,phoneNo,null);
        Driver driverObj = ofy().transact(new Work<Driver>(){

            @Override
            public Driver run() {
                Driver newdriver = new Driver(driverKey.getId(),driverRegForm);
                ofy().save().entity(newdriver);
                return newdriver;
            }
        });
    }


}