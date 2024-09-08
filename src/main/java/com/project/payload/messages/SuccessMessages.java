package com.project.payload.messages;

public class SuccessMessages {


    private SuccessMessages() {
    }

    public static final String PASSWORD_CHANGED_RESPONSE_MESSAGE = "Password Successfully Changed";
    public static final String PASSWORD_RESET_INSTRUCTIONS_SENT = "Password reset instructions have been sent to your e-mail address.";
    public static final String IMAGE_SAVED = "Image is Uploaded Successfully";

    // User Messages
    public static final String USER_CREATED = "User is Saved Successfully";
    public static final String USER_FOUND = "User is Found Successfully";
    public static final String USER_DELETED = "User is Deleted Successfully";
    public static final String USER_UPDATED = " User is Updated Successfully";  // bu satir ve alttaki satirin description kismini yer degistirdim, gozden gecirelim
    public static final String USER_UPDATE_MESSAGE = "Your information has been updated successfully";

    // Customer Messages
    public static final String CUSTOMER_SAVED = "Customer is Saved Successfully";
    public static final String CUSTOMER_UPDATED = "Customer is updated successfully ";
    public static final String CUSTOMER_DELETED = "Customer is Deleted Successfully";

    // Manager Messages
    public static final String MANAGER_SAVED= "Manager is Saved Successfully";
    public static final String MANAGER_UPDATED = "Manager is updated successfully ";
    public static final String MANAGER_DELETED = "Manager is Deleted Successfully";

    // Advert Messages
    public static final String ADVERT_SAVED = "Advert is Created and Wait for Approve";  // ADVERT_CREATE de yazilabilir, gozden gecirelim , mesaj olarak da "created" yerine "saved" yazilabilir
    public static final String ADVERT_UPDATED = "Advert is Updated Successfully";
    public static final String ADVERT_DELETED = "Advert is Deleted Successfully";
    public static final String ADVERT_FOUND = "Advert is Found Successfully";
    public static final String ADVERT_DECLINED = "Advert is Declined by Manager";
    public static final String RETURNED_POPULAR_ADVERTS= "Popular adverts returned for value";

    // Category Messages
    public static final String CATEGORY_SAVED= "Category is Saved Successfully";
    public static final String CATEGORY_UPDATED = "Category is updated successfully ";
    public static final String CATEGORY_DELETED = "Category is Deleted Successfully";

    // Tour Request Messages
    public static final String TOUR_REQUEST_SAVED = "Tour Request is Created Successfully";
    public static final String TOUR_REQUEST_UPDATED = "Tour Request is Updated Successfully";
    public static final String TOUR_REQUEST_APPROVED = "Tour Request is Accepted Successfully";
    public static final String TOUR_REQUEST_FOUND = "Tour Request is Found Successfully";
    public static final String TOUR_REQUEST_DECLINE = "Tour Request is Declined Successfully";
    public static final String TOUR_REQUEST_CANCELLED = "Tour Request is Canceled Successfully";
    public static final String TOUR_REQUEST_DELETED="Tour Request is Deleted Successfully";


}