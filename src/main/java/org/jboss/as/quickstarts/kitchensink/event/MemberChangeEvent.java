package org.jboss.as.quickstarts.kitchensink.event;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.context.ApplicationEvent;

public class MemberChangeEvent extends ApplicationEvent {
  private final Member member;
  
  public MemberChangeEvent(Object source, Member member) {
    super(source);
    this.member = member;
  }

  public Member getMember() {
    return member;
  }

}
