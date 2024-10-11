package org.jboss.as.quickstarts.kitchensink.dao;

import java.util.List;

import org.jboss.as.quickstarts.kitchensink.event.MemberChangeEvent;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import jakarta.annotation.PostConstruct;

@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class MemberListProducer {

  private MemberRepository memberRepository;

  private List<Member> members;
  
  @Autowired
  public MemberListProducer(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  // @Named provides access the return value via the EL variable name "members" in the UI (e.g.
  // Facelets or JSP view)
//  @Produces
//  @Named
  public List<Member> getMembers() {
      return members;
  }

  @EventListener(MemberChangeEvent.class)
  public void onMemberListChanged(final Member member) {
      retrieveAllMembersOrderedByName();
  }

  @PostConstruct
  public void retrieveAllMembersOrderedByName() {
      members = memberRepository.findAllByOrderByNameAsc().toList();
  }
}
