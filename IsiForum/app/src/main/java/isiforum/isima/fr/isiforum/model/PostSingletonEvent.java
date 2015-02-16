package isiforum.isima.fr.isiforum.model;

/**
 * Defines an event class that can be sent by the PostSingleton to its observers
 * (see Observer pattern).
 */
public class PostSingletonEvent {

    /**
     * This enum defines the different types of events that can be sent by the
     * PostSingleton.
     */
    public enum EventCode {
        POSTS_LIST_UPDATED,
        FAIL_TO_RETRIEVE_POSTS,
        FAIL_TO_SEND_POSTS,
        FAIL_TO_DELETE_POSTS
    }

    /**
     * A message to accompany the event.
     */
    private String mMessage;

    /**
     * The event's code.
     */
    private EventCode mCode;

    public PostSingletonEvent(String message, EventCode code){
        this.mMessage = message;
        this.mCode = code;
    }

    public PostSingletonEvent(EventCode code){
        this.mMessage = "";
        this.mCode = code;
    }

    public String getMessage(){
        return this.mMessage;
    }

    public EventCode getCode(){
        return this.mCode;
    }
}
