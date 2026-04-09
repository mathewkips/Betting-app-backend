
package com.betting_app.dashboard.test;

import com.google.firebase.database.FirebaseDatabase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirebaseTestController {

    @GetMapping("/api/test/firebase")
    public String testFirebase() {
        FirebaseDatabase.getInstance();
        return "Firebase connection is working";
    }
}