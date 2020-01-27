package terminals.selectfilters;

import org.springframework.data.jpa.domain.Specification;
import terminals.models.Registration;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class RegSpecifications {

    public static Specification<Registration> orderById() {
        return new Specification<Registration>() {
            @Override
            public Predicate toPredicate(Root<Registration> root,
                                         CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("recordId")));
                return criteriaQuery.getRestriction();
            }
        };
    }

    public static Specification<Registration> selectByRegId(String regId) {
        return new Specification<Registration>() {
            @Override
            public Predicate toPredicate(Root<Registration> root,
                                         CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(root.get("terminal").get("regId"), "%" + regId + "%");
            }
        };
    }

    public static Specification<Registration> selectByUserLogin(String login) {
        return new Specification<Registration>() {
            @Override
            public Predicate toPredicate(Root<Registration> root,
                                         CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(root.get("user").get("userLogin"), "%" + login + "%");
            }
        };
    }

    public static Specification<Registration> selectByUserFullName(String namePart) {
        return new Specification<Registration>() {
            @Override
            public Predicate toPredicate(Root<Registration> root,
                                         CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.or(criteriaBuilder.like(root.get("user").get("userName"), "%" + namePart + "%"),
                        criteriaBuilder.like(root.get("user").get("userSurname"), "%" + namePart + "%"));
            }
        };
    }

    public static Specification<Registration> selectByWhoGaveFullName(String namePart) {
        return new Specification<Registration>() {
            @Override
            public Predicate toPredicate(Root<Registration> root,
                                         CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.or(criteriaBuilder.like(root.get("adminGaveOut").get("userName"), "%" + namePart + "%"),
                        criteriaBuilder.like(root.get("adminGaveOut").get("userSurname"), "%" + namePart + "%"));
            }
        };
    }

    public static Specification<Registration> selectByWhoReceivedFullName(String namePart) {
        return new Specification<Registration>() {
            @Override
            public Predicate toPredicate(Root<Registration> root,
                                         CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.or(criteriaBuilder.like(root.get("adminGot").get("userName"), "%" + namePart + "%"),
                        criteriaBuilder.like(root.get("adminGot").get("userSurname"), "%" + namePart + "%"));
            }
        };
    }

    public static Specification<Registration> selectByStartDate(String from, String to) {
        String newFrom = from == null ? "1900-01-01" : from;
        String newTo = to == null ? "2999-01-01" : to;
        return new Specification<Registration>() {
            @Override
            public Predicate toPredicate(Root<Registration> root,
                                         CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.between(root.get("startDate"), newFrom, newTo);
            }
        };
    }

    public static Specification<Registration> selectByEndDate(String from, String to) {
        String newFrom = from == null ? "1900-01-01" : from;
        String newTo = to == null ? "2999-01-01" : to;
        return new Specification<Registration>() {
            @Override
            public Predicate toPredicate(Root<Registration> root,
                                         CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.between(root.get("endDate"), newFrom, newTo);
            }
        };
    }
}