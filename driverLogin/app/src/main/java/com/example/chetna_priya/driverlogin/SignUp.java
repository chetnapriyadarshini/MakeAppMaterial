package com.example.chetna_priya.driverlogin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.chetna_priya.myapplication.backend.driverApi.DriverApi;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    EditText firstName, lastName, email, phoneNo, password, address;
    ImageView reg_image;
    Button register;
    private DriverApi driverApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        driverApi = CloudEndpointBuilderHelper.getEndpoints();
        firstName = (EditText) findViewById(R.id.reg_firstname);
        lastName = (EditText) findViewById(R.id.reg_last_name);
        email =    (EditText) findViewById(R.id.reg_email);
        phoneNo =  (EditText) findViewById(R.id.reg_phone_num);
        password = (EditText) findViewById(R.id.reg_password);
        address =  (EditText) findViewById(R.id.reg_address);

        register = (Button) findViewById(R.id.btnRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allFieldsValid())
                {
                  //  driverApi.saveProfile(firstName.getText(),lastName.getText(),email.getText(),address.getText(),password.getText(),phoneNo.getText());
                    try {
                        driverApi.saveProfile(firstName.getText().toString(),
                                lastName.getText().toString(),
                                email.getText().toString(),
                                address.getText().toString(),
                                password.getText().toString(),
                                phoneNo.getText().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //  DriverApi driverApi = AbstractGoogleClient
                   // DriverApi.saveProfile(driverRegForm);
                }
            }
        });
    }

    private boolean allFieldsValid() {
        if(firstName == null)
        {
            makeToast("First name is mandatory!");
            return false;
        }
        if(lastName == null)
        {
            makeToast("Last name is mandatory!");
            return false;
        }
        if(email == null)
        {
            makeToast("Email is mandatory!");
            return false;
        }
        if(phoneNo == null)
        {
            makeToast("Phone No is mandatory!");
            return false;
        }
        if(address == null)
        {
            makeToast("Address is mandatory!");
            return false;
        }
        if(password == null)
        {
            makeToast("Passowrd is mandatory!");
            return false;
        }
        if(!isEmail(email.getText().toString()))
        {
            makeToast("Please enter a valid email");
            return false;
        }
        if(!(phoneNo.length() == 10))
        {
            makeToast("Please enter a valid phone number along with three digit area code");
            return false;
        }
        return true;
    }

    public boolean isEmail(String email)
    {
        boolean matchFound1;
        boolean returnResult=true;
        email=email.trim();
        if(email.equalsIgnoreCase(""))
            returnResult=false;
        else if(!Character.isLetter(email.charAt(0)))
            returnResult=false;
        else
        {
            Pattern p1 = Pattern.compile("^\\.|^\\@ |^_");
            Matcher m1 = p1.matcher(email.toString());
            matchFound1=m1.matches();

            Pattern p = Pattern.compile("^[a-zA-z0-9._-]+[@]{1}+[a-zA-Z0-9]+[.]{1}+[a-zA-Z]{2,4}$");
            // Match the given string with the pattern
            Matcher m = p.matcher(email.toString());

            // check whether match is found
            boolean matchFound = m.matches();

            StringTokenizer st = new StringTokenizer(email, ".");
            String lastToken = null;
            while (st.hasMoreTokens())
            {
                lastToken = st.nextToken();
            }
            if (matchFound && lastToken.length() >= 2
                    && email.length() - 1 != lastToken.length() && matchFound1==false)
            {

                returnResult= true;
            }
            else returnResult= false;
        }
        return returnResult;
    }

    private void makeToast(String message) {

        Toast showToast = Toast.makeText(this,message,Toast.LENGTH_LONG);
        showToast.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
