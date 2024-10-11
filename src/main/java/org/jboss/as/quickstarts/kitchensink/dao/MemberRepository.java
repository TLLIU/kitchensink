package org.jboss.as.quickstarts.kitchensink.dao;

import java.util.Optional;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>{
  
  public Optional<Member> findById(long id);
  
  public Optional<Member> findByEmail(String email);
  
  @Cacheable("memberListProducer")
  public Streamable<Member> findAllByOrderByNameAsc(); 

}
