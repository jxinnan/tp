package seedu.tab.model.student;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import seedu.tab.commons.util.StringUtil;
import seedu.tab.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Student}'s details match every search condition given.
 * Each term matches only its selected name, NUS Student ID, phone, email or tag field,
 * ignoring case and allowing partial matches.
 * A keyword made only of digits and the punctuation a phone number is written with
 * also matches the phone's digits with non-digit characters removed.
 */
public class DetailsContainsKeywordsPredicate implements Predicate<Student> {
    /** A keyword of digits and phone punctuation, holding at least one digit. */
    private static final Pattern PHONE_KEYWORD = Pattern.compile("[\\p{Nd}+().-]*\\p{Nd}[\\p{Nd}+().-]*");
    private static final Pattern NON_DIGIT = Pattern.compile("\\P{Nd}");

    /** The student field in which a search term must occur. */
    public enum Field { NAME, NUS_STUDENT_ID, PHONE, EMAIL, TAG }

    private final Map<Field, List<String>> keywordsByField;

    /** Creates a predicate requiring every term to match its selected field. */
    public DetailsContainsKeywordsPredicate(Map<Field, List<String>> keywordsByField) {
        this.keywordsByField = new EnumMap<>(Field.class);
        keywordsByField.forEach((field, keywords) -> this.keywordsByField.put(field, List.copyOf(keywords)));
    }

    @Override
    public boolean test(Student student) {
        return keywordsByField.values().stream().anyMatch(keywords -> !keywords.isEmpty())
                && keywordsByField.entrySet().stream().allMatch(entry -> entry.getValue().stream()
                        .allMatch(keyword -> matchesField(student, entry.getKey(), keyword)));
    }

    private static boolean matchesField(Student student, Field field, String keyword) {
        return switch (field) {
            case NAME -> StringUtil.containsIgnoreCase(student.getName().fullName, keyword);
            case NUS_STUDENT_ID -> StringUtil.containsIgnoreCase(
                    student.getNusStudentId().map(NusStudentId::toString).orElse(""), keyword);
            case PHONE -> matchesPhone(student.getPhone(), keyword);
            case EMAIL -> StringUtil.containsIgnoreCase(student.getEmail().map(Email::toString).orElse(""), keyword);
            case TAG -> matchesAnyTag(student, keyword);
            default -> throw new IllegalArgumentException("Unknown search field: " + field);
        };
    }

    private static boolean matchesPhone(Phone phone, String keyword) {
        return StringUtil.containsIgnoreCase(phone.value, keyword)
                || matchesPhoneByDigits(phone, keyword);
    }

    /**
     * Returns true if the digits {@code keyword} holds appear in {@code phone} in order.
     * The keyword must be one a user would type as a number: made only of digits and the
     * punctuation a phone number is written with, and holding at least one digit, so a
     * keyword such as {@code ---} or one drawn from a name or a tag stays with the literal
     * comparison above.
     */
    private static boolean matchesPhoneByDigits(Phone phone, String keyword) {
        if (!PHONE_KEYWORD.matcher(keyword).matches()) {
            return false;
        }
        String digits = NON_DIGIT.matcher(keyword).replaceAll("");
        return phone.getDigits().contains(digits);
    }

    private static boolean matchesAnyTag(Student student, String keyword) {
        return student.getTags().stream().anyMatch(tag -> StringUtil.containsIgnoreCase(tag.tagName, keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DetailsContainsKeywordsPredicate otherPredicate)) {
            return false;
        }

        return keywordsByField.equals(otherPredicate.keywordsByField);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywordsByField", keywordsByField).toString();
    }
}

