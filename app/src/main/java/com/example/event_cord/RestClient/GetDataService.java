package com.example.event_cord.RestClient;

import com.example.event_cord.model.Event;
import com.example.event_cord.model.Notice;
import com.example.event_cord.model.User;
import com.example.event_cord.model.UserEventPair;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GetDataService {
    @GET("/api/users")
    Call<List<User>> getAllUsers();

    @GET("/api/events/user/{id}")
    Call<List<Event>> getMyEvents(@Path("id") Integer id);

    @GET("/api/events/all/{id}")
    Call<List<Event>> getAllEvents(@Path("id") Integer id);

    @POST("/api/event")
    Call<UserEventPair> createEvent(@Body Event event);

    @POST("/api/login")
    Call<User> getLoginUser(@Body User user);

    @POST("/api/user")
    Call<User> createUser(@Body User user);

    @GET("/api/notices/event/{id}")
    Call<List<Notice>> getNoticeByEvent(@Path("id") Integer id);

    @POST("/api/add_user_to_event/")
    Call<UserEventPair> joinEvent(@Body UserEventPair userEventPair);

    @POST("/api/remove_user_from_event/")
    Call<UserEventPair> leaveEvent(@Body UserEventPair userEventPair);

    @GET("/api/user_event/{user_id}/{event_id}")
    Call<ResponseBody> checkUserEvent(@Path("user_id") Integer userId, @Path("event_id") Integer eventId);

    @GET("/api/notices/event/{event_id}")
    Call<List<Notice>> getNotices(@Path("event_id") Integer eventId);

    @POST("/api/notice/")
    Call<Notice> createNotice(@Body Notice notice);

    @DELETE("/api/event/{id}")
    Call<Void> deleteEvent(@Path("id") int eventId);

    @DELETE("/api/notice/{id}")
    Call<Void> deleteNotice(@Path("id") int noticeId);

    @PATCH("/api/event/")
    Call<Void> updateEvent(@Body Event event);

    @PATCH("/api/notice")
    Call<Void> updateNotice(@Body Notice notice);
}
