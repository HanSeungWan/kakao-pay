package com.kakao.pay.db.repo;

import com.kakao.pay.db.dto.SprinkleMoneyDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprinkleMoneyRepo extends MongoRepository<SprinkleMoneyDto, Long> {
    public SprinkleMoneyDto findByToken(String token);
    public SprinkleMoneyDto findByTokenAndRoomId(String token, String roomId);
}
