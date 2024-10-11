package org.jboss.as.quickstarts.kitchensink.dao;

import java.util.Optional;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.WebApplicationContext;

@Repository
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public interface MemberRepository extends JpaRepository<Member, Long>{
  
  public Optional<Member> findById(long id);
  
  public Optional<Member> findByEmail(String email);
  
  @Cacheable("memberListProducer")
  public Streamable<Member> findAllByOrderByNameAsc(); 
  
  @SuppressWarnings("unchecked")
  @CacheEvict("memberListProducer")
  public Member save(Member member);

}
