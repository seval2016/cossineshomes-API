package com.project.payload.messages;

public class SuccessMessages {

    public static final String PASSWORD_RESET_SUCCESSFULLY="";


    private SuccessMessages() {
    }

    public static final String PASSWORD_CHANGED_RESPONSE_MESSAGE = "Password Successfully Changed";
    public static final String PASSWORD_RESET_INSTRUCTIONS_SENT = "Password reset instructions have been sent to your e-mail address.";
    public static final String IMAGE_SAVED = "Image is Uploaded Successfully";

    // User Messages
    public static final String USER_CREATED = "User is Saved Successfully";
    public static final String USER_FOUND = "User is Found Successfully";
    public static final String USER_DELETED = "User is Deleted Successfully";
    public static final String USER_UPDATED = " User is Updated Successfully";
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
    public static final String ADVERT_SAVED = "Advert is Created and Wait for Approve";
    public static final String ADVERT_UPDATED = "Advert is Updated Successfully";
    public static final String ADVERT_DELETED = "Advert is Deleted Successfully";
    public static final String ADVERT_FOUND = "Advert is Found Successfully";
    public static final String ADVERT_DECLINED = "Advert is Declined by Manager";
    public static final String RETURNED_POPULAR_ADVERTS= "Popular adverts returned for value";

    //Advert_type

    public static final String ADVERT_TYPE_SAVED = "Advert type saved";
    public static final String ADVERT_TYPE_DELETED = "Advert type deleted";
    public static final String ADVERT_TYPE_UPDATED = "Advert type updated";

    // Category Messages
    public static final String CATEGORY_SAVED= "Category is Saved Successfully";
    public static final String CATEGORY_UPDATED = "Category is updated successfully ";
    public static final String CATEGORY_DELETED = "Category is Deleted Successfully";
    public static final String CATEGORY_FOUNDED = "Category successfully found.";
    public static final String CATEGORY_PROPERTY_KEYS_FETCHED = "Category property keys have been successfully retrieved.";
    public static final String CATEGORY_PROPERTY_KEY_CREATED_SUCCESS = "Category property key has been successfully created.";
    public static final String CATEGORY_PROPERTY_KEYS_RETRIEVED = "Category property keys have been successfully retrieved.";
    public static final String CATEGORY_PROPERTY_KEY_UPDATED_SUCCESS = "The property key was successfully updated.";
    public static final String CATEGORY_PROPERTY_KEY_DELETED_SUCCESS = "Property key deleted successfully.";



    // Tour Request Messages
    public static final String TOUR_REQUEST_SAVED = "Tour Request is Created Successfully";
    public static final String TOUR_REQUEST_UPDATED = "Tour Request is Updated Successfully";
    public static final String TOUR_REQUEST_APPROVED = "Tour Request is Accepted Successfully";
    public static final String TOUR_REQUEST_FOUND = "Tour Request is Found Successfully";
    public static final String TOUR_REQUEST_DECLINE = "Tour Request is Declined Successfully";
    public static final String TOUR_REQUEST_CANCELLED = "Tour Request is Canceled Successfully";
    public static final String TOUR_REQUEST_DELETED="Tour Request is Deleted Successfully";

    // Contact Messages
    public static final String CONTACT_MESSAGE_CREATED = "Contact Message is Created Successfully";
    public static final String CONTACT_MESSAGE_UPDATED = "Contact Message is Created Successfully";
    public static final String CONTACT_MESSAGE_DELETED = "Contact Message is Created Successfully";
    public static final String CONTACT_MESSAGE_CANCELLED = "Contact Message is Created Successfully";


    //Favorites
    public static final String ALL_FAVORITES_DELETED = "All Favorites Deleted Successfully";
    public static final String ALL_FAVORITES_DELETED_BY_ID = "All Favorites Deleted Succesfully with id %s";
    public static final String FAVORITE_DELETED_BY_ID = "Favorite Deleted Successfully with id %s";

}