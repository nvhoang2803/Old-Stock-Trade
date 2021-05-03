package com.example.oldstocktrade.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAuWtzCqc:APA91bFdAol7-Giy0saalJdS5hTAUw_aWYgws8KK8XCyh4rsG5JMb8CK8YrXPnQUWbXGqOz5n2wS3rVuN9J7vuLKvRGdWznUyzun72yjpJrHnj90Zwv1TqAOusxB4RKd-zIFHVDuEvLA"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
