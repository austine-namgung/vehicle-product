package com.example.product.messaging;


import com.example.product.model.ResultMessage;
import com.example.product.model.VehicleProduct;
import com.example.product.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessagingManager {
	private final ProductRepository repository;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper mapper;

	private static final String PRODUCT_TOPIC = "product-topic";
	private static final String SAGA_RREPLY_TOPIC = "saga-reply-topic";
	private static final String PRODUCT_COMPENSATE_TOPIC = "product-cancel-topic";
	private static final String SAGA_COMPENSATE_RREPLY_TOPIC = "saga-cancel-reply-topic";

	@KafkaListener(topics = PRODUCT_TOPIC, groupId = "product")
	public void listen(String message) throws JsonMappingException, JsonProcessingException {
	
		log.info("====== Product_Topic 에서  - Consumer 도착 ======= message: {}", message);  
		try{
			//////////// 장애발생 /////////
			if(false){
				log.info("==[에러발생]=== Product 서비스 장애 발생  ");
				throw new Exception("error 발생");
			}else{				
				VehicleProduct product  = mapper.readValue(message,new TypeReference<VehicleProduct>(){});
				
				repository.save(product);
				log.info("===== Order 요청사항   vehicle_product 테이블에 저장 완료 ");
				sendToReplyTopic(message);
			}
		}catch(Exception e){			
			faileSendToReplyTopic(message);
		}
		
        
	}
    public void sendToReplyTopic(String message) throws JsonProcessingException {
		ResultMessage resultMessage = new ResultMessage();
		resultMessage.setSuccessYn("Y");
		resultMessage.setTxCode("S");
		resultMessage.setServiceCode("product");
		resultMessage.setJasonTx(message);
		resultMessage.setMessage("정상적으로 저장되었습니다");
		String resultString = mapper.writeValueAsString(resultMessage);

		log.info("======  saga-reply-topic에 정상 완료 결과 전송  전송메세지: {}" , resultString);
        kafkaTemplate.send(SAGA_RREPLY_TOPIC, resultString);  

	}
	public void faileSendToReplyTopic(String message) throws JsonProcessingException {
		
		ResultMessage resultMessage = new ResultMessage();
		resultMessage.setSuccessYn("N");
		resultMessage.setTxCode("S");
		resultMessage.setServiceCode("product");
		resultMessage.setJasonTx(message);
		resultMessage.setMessage("저장중 에러가 발생하였습니다");
		String resultString = mapper.writeValueAsString(resultMessage);
		
		log.info("==[에러 전송]=== Prodct의 에러  SAGA_RREPLY_TOPIC 에   서비스 장애 상황 메세지 전달  전송메세지: {}", resultString);
		
		kafkaTemplate.send(SAGA_RREPLY_TOPIC, resultString);  

    }

	

	@KafkaListener(topics = PRODUCT_COMPENSATE_TOPIC, groupId = "product")
	public void cancelListen(String message) throws JsonMappingException, JsonProcessingException {
		log.info("====[보상처리]  Order에서 보낸 보상처리 요청 수신 === message: {}", message);
		 
		VehicleProduct product  = mapper.readValue(message,new TypeReference<VehicleProduct>(){});
		
		repository.deleteById(product.getOrderTx());
		log.info("====[보상처리]  Vehicle_product 테이블에서 해당 트랜잭션 데이터 삭재 정상처리 완료 ");
		
		cancelSendToReplyTopic(message);
        
	}
	public void cancelSendToReplyTopic(String message) throws JsonProcessingException {
		log.info("===========cancelSendToReplyTopic 호출 =  : ");
		ResultMessage resultMessage = new ResultMessage();
		resultMessage.setSuccessYn("Y");
		resultMessage.setTxCode("C");
		resultMessage.setServiceCode("product");
		resultMessage.setJasonTx(message);
		resultMessage.setMessage("정상적으로 취소되었습니다");
		String resultString = mapper.writeValueAsString(resultMessage);

		log.info("====[보상처리]  Product 보상 처리 결과 전송 (Topic : SAGA_COMPENSATE_RREPLY_TOPIC)  message: {}",resultString);
        kafkaTemplate.send(SAGA_COMPENSATE_RREPLY_TOPIC, resultString);  

	}

    
	
	
    
}
