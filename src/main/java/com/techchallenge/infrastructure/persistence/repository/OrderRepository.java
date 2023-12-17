package com.techchallenge.infrastructure.persistence.repository;

import com.techchallenge.infrastructure.persistence.document.OrderDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<OrderDocument, String> {
	
	//@Query("Select o from OrderEntity o ORDER BY o.dateOrderInit ASC ")
	Page<OrderDocument> findAllOrderByDateOrderInit(Pageable pageable);

	Optional<List<OrderDocument>> findByStatusOrder(String recebido);

	//@Query("Select o from OrderEntity o where o.statusOrder <> 'FINALIZADO' ORDER BY FIELD(o.statusOrder, 'PRONTO', 'EM_PREPARACAO', 'RECEBIDO'), dateOrderInit ASC ")
	Optional<List<OrderDocument>> findByStatusOrderAndDateOrderInit();

	
}
