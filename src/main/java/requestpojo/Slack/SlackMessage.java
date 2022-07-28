package requestpojo.Slack;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "message",
        "to",
        "shared_secret"
})
public class SlackMessage {

    @JsonProperty("message")
    private String message;
    @JsonProperty("to")
    private String to;
    @JsonProperty("shared_secret")
    private String sharedSecret;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("to")
    public String getTo() {
        return to;
    }

    @JsonProperty("to")
    public void setTo(String to) {
        this.to = to;
    }

    @JsonProperty("shared_secret")
    public String getSharedSecret() {
        return sharedSecret;
    }

    @JsonProperty("shared_secret")
    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}