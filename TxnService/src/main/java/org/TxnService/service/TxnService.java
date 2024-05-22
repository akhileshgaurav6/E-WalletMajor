package org.TxnService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.TxnService.model.Txn;
import org.TxnService.model.TxnStatus;
import org.TxnService.repository.TxnRepository;
import org.Utils.CommonConstants;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class TxnService implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TxnRepository txnRepository;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        HttpHeaders header = new HttpHeaders();
        header.setBasicAuth("txn-service" , "txn-service");
        HttpEntity request = new HttpEntity(header);
        JSONObject jsonObject = restTemplate.exchange("http://localhost:9091/user/getUser?contact="+username, HttpMethod.GET,request, JSONObject.class).getBody();
        User user = new User((String)jsonObject.get("username"), (String)jsonObject.get("password"),(List<GrantedAuthority>)jsonObject.get("authority"));
        return user;

    }

    public String initTxn(String username, String receiver, String amount, String purpose) {
        Txn txn = Txn.builder().
                txnId(UUID.randomUUID().toString()).
                senderId(username).
                txnAmount(Double.valueOf(amount)).
                receiverId(receiver).
                purpose(purpose).
                txnStatus(TxnStatus.INITIATED).
                build();

        txnRepository.save(txn);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CommonConstants.TXN_INITIATED_TOPIC_AMOUNT,amount);
        jsonObject.put(CommonConstants.TXN_INITIATED_TOPIC_TXNID, txn.getTxnId());
        jsonObject.put(CommonConstants.TXN_INITIATED_TOPIC_RECEIVER, receiver);
        jsonObject.put(CommonConstants.TXN_INITIATED_TOPIC_SENDER, username);

        kafkaTemplate.send(CommonConstants.TXN_INITIATED_TOPIC, jsonObject);
        return txn.getTxnId();

    }

    @KafkaListener(topics = CommonConstants.TXN_UPDATED_TOPIC, groupId = "txn-group")
    public void updatetxn(String msg) throws JsonProcessingException {
        JSONObject object  = new JSONObject();
        String txnid = (String) object.get(CommonConstants.TXN_UPDATED_TOPIC_TXNID);
        String status = (String) object.get(CommonConstants.TXN_UPDATED_TOPIC_STATUS);
        String message = (String) object.get(CommonConstants.TXN_UPDATED_TOPIC_MESSAGE);

        txnRepository.updateTxnStatus(txnid, TxnStatus.valueOf(status), message);
    }

    public List<Txn> getTxns(String username, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Txn> pageDate = txnRepository.findBySenderId(username,pageable);
        return pageDate.getContent();

    }
}
