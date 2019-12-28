package io.github.hobbstech.bpm.commons;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.validation.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.negate;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Validations {

    public static <T> void checkNonNull(T t, Supplier<? extends RuntimeException> exceptionSupplier) {
        try {
            Objects.requireNonNull(t);
        } catch (NullPointerException ex) {
            throw exceptionSupplier.get();
        }
    }

    public static <T> void checkArgumentNonNull(T t, final String fieldName, Supplier<? extends RuntimeException> exceptionSupplier) {
        checkNonNull(t, () -> new IllegalArgumentException("Target object can not be null"));
        try {

            val field = getField(t, fieldName);

            val value = field.get(t);

            checkNonNull(value, exceptionSupplier);

        } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format("Failed to access the field value due to %s", e.getMessage()));
        }

    }

    public static <T> void checkArgumentNonNull(T t, final String fieldName, String message) {
        checkArgumentNonNull(t, fieldName, () -> new IllegalStateException(message));

    }

    public static <T> void checkStringArgumentNonNullAndNonEmpty(T t, final String fieldName, String message) {

        checkNonNull(t, () -> new IllegalArgumentException("Target object can not be null"));
        try {

            val field = getField(t, fieldName);

            val value = field.get(t);

            checkNonNull(value, () -> new IllegalStateException(message));

            if (value.getClass().isAssignableFrom(String.class)) {

                val string = (String) value;

                if (string.trim().isEmpty())
                    throw new IllegalStateException(message);

            }

        } catch (Exception e) {
            throw new IllegalStateException(String.format("Failed to access the field value due to %s", e.getMessage()));
        }

    }

    private static <T> Field getField(T t, String fieldName) {

        Class clazz = t.getClass();

        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());

        Optional<Field> optionalField = fields.stream()
                .filter(field1 -> field1.getName().equals(fieldName))
                .findFirst();

        while (negate(optionalField.isPresent())) {

            log.debug("---> Field with name {} was not found in class {} now searching in {}", fieldName, clazz, clazz.getSuperclass());

            clazz = clazz.getSuperclass();

            if (nonNull(clazz)) {

                fields = Arrays.asList(clazz.getDeclaredFields());

                optionalField = fields.stream()
                        .filter(field1 -> field1.getName().equals(fieldName))
                        .findFirst();
            }

        }

        val field = optionalField
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("Field with name %s was not found on %s", fieldName, t.getClass().getSimpleName())));

        if (!field.isAccessible())
            field.setAccessible(true);

        return field;

    }

    public static <T> void validate(T t) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<T>> violations = validator.validate(t);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
    }

    public static void requireNonNull(Object target, String message) {
        if (target == null) {
            throw new NullPointerException(message);
        }
    }

    public static <X extends Throwable> void requireNonNull(Object target, Supplier<? super X> supplier) throws X {
        if (target == null) {
            throw (X) supplier.get();
        }
    }

    public static <T> Boolean nonEquals(T obj1, T obj2) {
        return obj1 != obj2 && !obj1.equals(obj2);
    }

}
