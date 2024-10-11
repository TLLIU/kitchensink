package org.jboss.as.quickstarts.kitchensink.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.as.quickstarts.kitchensink.dao.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Pattern;

@Controller
@RequestMapping(value="/members", produces= {"application/json"})
public class MemberResourceRESTService {
  private static final Logger log = LoggerFactory.getLogger(MemberResourceRESTService.class);
  private final MemberRepository repository;
  
  private final MemberRegistration registration;
  
  @Autowired
  public MemberResourceRESTService(MemberRepository repository, MemberRegistration registration) {
    this.repository = repository;
    this.registration = registration;
  }
  
  @GetMapping(value="")
  public List<Member> listAllMembers() {
      return repository.findAllByOrderByNameAsc().toList();
  }

  @GetMapping(value="/{id}")
  public Member lookupMemberById(@PathVariable("id") @Pattern(regexp = "[0-9][0-9]*") long id) {
    return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  /**
   * Creates a new member from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
   * or with a map of fields, and related errors.
   */
  @PostMapping(value="", consumes= {"application/json"})
  public ResponseEntity<Map<String, String>> createMember(@RequestParam @Validated Member member) {
      try {
          // Validates member using bean validation
          validateMember(member);

          registration.register(member);

          // Create an "ok" response
          return ResponseEntity.ok().build();
      } catch (ConstraintViolationException ce) {
          // Handle bean validation issues
        Map<String, String> responseObj = createViolationResponse(ce.getConstraintViolations());
        return ResponseEntity.badRequest().body(responseObj);
      } catch (ValidationException e) {
          // Handle the unique constrain violation
          Map<String, String> responseObj = new HashMap<>();
          responseObj.put("email", "Email taken");
          return ResponseEntity.status(HttpStatus.CONFLICT).body(responseObj);
      } catch (Exception e) {
          // Handle generic exceptions
          Map<String, String> responseObj = new HashMap<>();
          responseObj.put("error", e.getMessage());
          return ResponseEntity.badRequest().body(responseObj);
      }
  }

  /**
   * <p>
   * Validates the given Member variable and throws validation exceptions based on the type of error. If the error is standard
   * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.
   * </p>
   * <p>
   * If the error is caused because an existing member with the same email is registered it throws a regular validation
   * exception so that it can be interpreted separately.
   * </p>
   *
   * @param member Member to be validated
   * @throws ConstraintViolationException If Bean Validation errors exist
   * @throws ValidationException If member with the same email already exists
   */
  private void validateMember(Member member) throws ConstraintViolationException, ValidationException {
      // Create a bean validator and check for issues.
//      Set<ConstraintViolation<Member>> violations = Validator.validate(member);
//
//      if (!violations.isEmpty()) {
//          throw new ConstraintViolationException(new HashSet<>(violations));
//      }

      // Check the uniqueness of the email address
      if (emailAlreadyExists(member.getEmail())) {
          throw new ValidationException("Unique Email Violation");
      }
  }

  /**
   * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
   * by clients to show violations.
   *
   * @param violations A set of violations that needs to be reported
   * @return JAX-RS response containing all violations
   */
  private Map<String, String> createViolationResponse(Set<ConstraintViolation<?>> violations) {
      log.debug("Validation completed. violations found: " + violations.size());

      Map<String, String> responseObj = new HashMap<>();

      for (ConstraintViolation<?> violation : violations) {
          responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
      }

      return responseObj;
  }

  /**
   * Checks if a member with the same email address is already registered. This is the only way to easily capture the
   * "@UniqueConstraint(columnNames = "email")" constraint from the Member class.
   *
   * @param email The email to check
   * @return True if the email already exists, and false otherwise
   */
  public boolean emailAlreadyExists(String email) {
    return repository.findByEmail(email).isPresent();
  }
}
