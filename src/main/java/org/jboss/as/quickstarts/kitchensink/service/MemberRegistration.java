package org.jboss.as.quickstarts.kitchensink.service;

import org.jboss.as.quickstarts.kitchensink.dao.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.event.MemberChangeEvent;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class MemberRegistration {

  private Logger log = LoggerFactory.getLogger(MemberRegistration.class);

  private final MemberRepository repository;
  private ApplicationEventPublisher applicationEventPublisher;
  
  @Autowired
  public MemberRegistration(MemberRepository repository, ApplicationEventPublisher applicationEventPublisher) {
    this.repository = repository;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void register(Member member) throws Exception {
      log.info("Registering " + member.getName());
      repository.save(member);
      applicationEventPublisher.publishEvent(new MemberChangeEvent(this, member));
  }
}
