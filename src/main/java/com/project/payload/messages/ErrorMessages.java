package com.project.payload.messages;

public class ErrorMessages {


    public static final String RESET_CODE_IS_NOT_FOUND = "Reset code is not found";
    public static final String BEGIN_TIME_CAN_NOT_BE_AFTER_END_TIME = "Start time can not be later than end time";
    public static final String EXCEL_COULD_NOT_BE_CREATED = "Excel file could not be created.";
    public static final String EXCEL_COULD_NOT_BE_CREATED_TYPE_IS_NOT_ADVERT = "Excel file could not be created, type is not advert.";
    public static final String ADVERT_STATUS_NOT_FOUND = "Advert status not found.";
    public static final String THERE_IS_NO_USER_REGISTERED_WITH_THIS_EMAIL_ADDRESS = "There is no user registered with this email address";
    public static final String TOUR_REQUEST_CAN_NOT_BE_CHANGED = "Tour request cannot be changed after it has been submitted.";
    public static final String THERE_IS_NO_TOUR_REQUEST_OF_ADVERT = "There is no tour request associated with this advert.";
    public static final String THE_USER_HAS_RELATED_RECORDS_WITH_ADVERTS_OR_TOUR_REQUESTS ="The user has related records with adverts or tour requests." ;


    private ErrorMessages() {
    }


    public static final String PROPERTY_VALUE_NOT_FOUND = "Property value not found";

    // General Errors
    public static final String DATABASE_CONNECTION_ERROR = "Error connecting to the database. Please try again later.";
    public static final String DATABASE_QUERY_ERROR = "Error executing database query. Please check your input or contact support.";
    public static final String UNAUTHORIZED_ACCESS = "You are not authorized to perform this action.";
    public static final String INVALID_INPUT_FORMAT = "Invalid input format. Please enter the correct information.";
    public static final String REQUIRED_FIELD_MISSING = "A required field is missing. Please fill out all required fields.";
    public static final String UNKNOWN_ERROR = "An unknown error occurred. Please contact support.";
    public static final String THIS_PHONE_NUMBER_IS_ALREADY_TAKEN = "This phone %s number is already taken";
    public static final String NOT_HAVE_AUTHORITY = "You don't have authority";
    public static final String NOT_PERMITTED_METHOD_MESSAGE = "You do not have any permission to do this operation because the user is built-in.";


    //Email
    public static final String EMAIL_IS_INCORRECT = " %s, Email is not correct";
    public static final String THIS_EMAIL_IS_ALREADY_TAKEN = "This email %s is already taken";


    //password
    public static final String PASSWORD_IS_NOT_CORRECT = "Password do not matched";
    public static final String THE_PASSWORDS_ARE_NOT_MATCHED = "The passwords entered do not match. Please re-enter your password";
    public static final String RESET_PASSWORD_CODE_DID_NOT_MATCH = "The reset password code didn`t match";
    public static final String PASSWORDS_DID_NOT_MATCH ="Password did not match" ;
    public static final String PASSWORD_IS_INCCORECT = "Password is incorrect";



    // User Errors
    public static final String PASSWORD_NOT_MATCHED = "Error : Your passwords are not matched";
    public static final String ALREADY_REGISTER_MESSAGE_USERNAME = "Error : User with username %s is already registered";
    public static final String ALREADY_REGISTER_MESSAGE_SSN = "Error : User with ssn %s is already registered";
    public static final String ALREADY_REGISTER_MESSAGE_EMAIL = "Error : User with email %s is already registered";
    public static final String ALREADY_REGISTER_MESSAGE_PHONE = "Error : User with phone number %s is already registered";
    public static final String ROLE_NOT_FOUND = "Error : There is no role like that, check the database";
    public static final String NOT_FOUND_USER_USER_ROLE_MESSAGE = "Error: User not found with user-role %s";
    public static final String NOT_FOUND_USER_MESSAGE = "Error: User not found with id %s";
    public static final String NOT_FOUND_USER_EMAIL = "There is no email that matches %s";
    public static final String USER_IS_NOT_FOUND = "The user with %s id is not found";
    public static final String USER_IS_NOT_FOUND_BY_EMAIL = "The user with %s email is not found";
    public static final String NOT_FOUND_USER_WITH_ROLE_MESSAGE = "Error: The role information of the user with id %s is not role: %s";
    public static final String USER_ROLE_DELETE_ERROR = "Error : User Role is not deleted";
    public static final String BUILT_IN_USER_CAN_NOT_BE_UPDATED = "BuiltIn user can not be updated";
    public static final String BUILT_IN_USER_CAN_NOT_BE_DELETED = "BuiltIn user can not be deleted";
    public static final String USER_HAS_NOT_CUSTOMER_ROLE = "User is not customer";



    // Advert Errors
    public static final String ADVERT_NOT_FOUND = "Error : Advert is not found.";
    public static final String ADVERT_CREATION_ERROR = "Error : Advert is not created. Please try again.";
    public static final String ADVERT_UPDATE_ERROR = "Error : Advert Update is not updated.";
    public static final String ADVERT_DELETE_ERROR = "Error : Advert is not deleted";
    public static final String START_PRICE_AND_END_PRICE_INVALID="Start price must be less than end price";

    // Country Errors
    public static final String COUNTRY_NOT_FOUND = "Error : Country does not exist.";
    public static final String COUNTRY_CREATION_ERROR = "Error : Country is not created. Please try again.";
    public static final String COUNTRY_UPDATE_ERROR = "Error : Country is not updated.";
    public static final String COUNTRY_DELETE_ERROR = "Error : Country is not deleted.";



    // City Errors
    public static final String CITY_NOT_FOUND = "Error : City does not exist.";
    public static final String CITY_CREATION_ERROR = "Error : City is not created. Please try again.";
    public static final String CITY_UPDATE_ERROR = "Error : City is not updated";
    public static final String CITY_DELETE_ERROR = "Error : City is not deleted";


    // District Errors
    public static final String DISTRICT_NOT_FOUND = "Error : District does not exist.";
    public static final String DISTRICT_CREATION_ERROR = "Error : District is not created. Please try again.";
    public static final String DISTRICT_UPDATE_ERROR = "Error : District is not updated.";
    public static final String DISTRICT_DELETE_ERROR = "Error : District is not deleted.";


    // Category Errors
    public static final String CATEGORY_NOT_FOUND = "Error : Category does not exist.";
    public static final String CATEGORY_CREATION_ERROR = "Error : Category is not created. Please try again.";
    public static final String CATEGORY_UPDATE_ERROR = "Error : Category is not updated.";
    public static final String CATEGORY_DELETE_ERROR = "Error : Category is not deleted.";
    public static final String CATEGORY_CANNOT_UPDATE =  "Error : Cannot be updated because the category is built_in";;
    public static final String CATEGORY_HAS_ADVERTS = "Category has related adverts, deletion is not allowed.";
    public static final String CATEGORY_PROPERTY_KEY_ALREADY_EXIST = "A category property key with the same name already exists.";
    public static final String CATEGORY_PROPERTY_KEY_CANNOT_UPDATE = "The category property key cannot be updated because it is marked as built-in.";
    public static final String CATEGORY_PROPERTY_KEY_NOT_FOUND = "The property key with the specified ID was not found.";


    // Favorite Errors
    public static final String FAVORITE_NOT_FOUND = "Error : Favorite does not exist.";
    public static final String FAVORITE_CREATION_ERROR = "Error : Favorite is not created. Please try again.";
    public static final String FAVORITE_DELETE_ERROR = "Error : Favorite is not deleted. Please ensure it exists.";


    // TourRequest Errors
    public static final String TOUR_REQUEST_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE = "Error: The start date cannot be earlier than the last registration date";
    public static final String TOUR_REQUEST_END_DATE_IS_EARLIER_THAN_START_DATE = "Error: The end date cannot be earlier than start date";
    public static final String TOUR_REQUEST_NOT_FOUND = "TError : Tour Request does not exist.";
    public static final String TOUR_REQUEST_CREATION_ERROR = "Error : Tour Request is not created. Please try again.";
    public static final String TOUR_REQUEST_UPDATE_ERROR = "Error : Tour Request is not updated. Please check your input.";
    public static final String TOUR_REQUEST_DELETE_ERROR = "Error : Tour Request is not deleted. Please ensure it exists.";

    public static final String CONFLICT_TOUR_REQUEST_TIME = "Error: There is an appointment for the tour you requested";

    // Image Errors
    public static final String IMAGE_NOT_FOUND = "Error : The requested image does not exist.";
    public static final String IMAGE_UPLOAD_ERROR = "Error uploading the image. Please try again.";
    public static final String IMAGE_DELETE_ERROR = "Error deleting the image. Please ensure it exists.";
    public static final String IMAGE_FORMAT_FAILED= "Error : The image format is invalid or not supported";
    // AdvertType Errors
    public static final String ADVERT_TYPE_NOT_FOUND = "Error : Advert Type does not exist.";
    public static final String ADVERT_TYPE_CREATION_ERROR = "Error : Advert Type is not created. Please try again.";
    public static final String ADVERT_TYPE_UPDATE_ERROR = "Error : Advert Type is not updated. Please check your input.";
    public static final String ADVERT_TYPE_DELETE_ERROR = "Error : Advert Type is not deleted. It may be associated with adverts.";
    public static final String ADVERT_TYPE_NOT_FOUND_BY_ID = "Error : Advert Type is not fount with id:%s";

    public static final String NOT_FOUND_ADVERT_WITH_ID_MESSAGE ="Error: Advert with id: %s not found";
    public static final String ADVERT_IS_NOT_FOUND_FOR_USER ="Error: Advert is not found with user id: %s";
    public static final String THIS_ADVERT_DOES_NOT_UPDATE ="Error: This advert is built in";
    public static final String ADVERT_TYPE_ALREADY_EXIST= "Advert Type already exists";




}