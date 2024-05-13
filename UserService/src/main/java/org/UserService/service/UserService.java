package org.UserService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.Utils.CommonConstants;
import org.UserService.model.User;
import org.UserService.repository.UserRepository;
import org.UserService.request.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Value("${user.authority}")
    private String userAuthority;
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate; //here string string is topic name and message.
    @Autowired
    private ObjectMapper objectMapper;

    public User createUser(CreateUserRequest createUserRequest) throws JsonProcessingException {
       User user = createUserRequest.toUser();
        user.setAuthority(userAuthority);
        user.setPassword(encoder.encode(createUserRequest.getPassword()));
//        user.setUserStatus(UserStatus.ACTIVE);
//        user.setUserType(UserType.USER);
        userRepository.save(user);
        // onc the user is created i have to send a notification

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_NAME, StringUtils.isEmpty(user.getName()) ? "USER" : user.getName());
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_EMAIL, user.getEmail());
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_PHONE_NO, user.getPhoneNo());
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_USERIDENTIFIER, user.getUserIdentifier());
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_USERIDENTIFIER_VALUE, user.getUserIdentifierValue());
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_ID, user.getId());
        kafkaTemplate.send(CommonConstants.USER_CREATION_TOPIC, objectMapper.writeValueAsString(jsonObject));
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByPhoneNo(username);
    }
}
