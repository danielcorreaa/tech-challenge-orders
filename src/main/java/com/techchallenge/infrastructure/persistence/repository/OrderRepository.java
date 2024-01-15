package com.techchallenge.infrastructure.persistence.repository;

import com.techchallenge.infrastructure.persistence.document.OrderDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<OrderDocument, String> {
	

	Page<OrderDocument> findAllOrderByDateOrderInit(Pageable pageable);

	List<OrderDocument> findByStatusOrder(String recebido);

	@Query("{'statusOrder':{ $ne: 'FINALIZADO'}}")
	List<OrderDocument> findByStatusOrderAndDateOrderInit(Sort sort);


	@Query("{'sent': false}")
	List<OrderDocument> findOrdersNotSent();
}
