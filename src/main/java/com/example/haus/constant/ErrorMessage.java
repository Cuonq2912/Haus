package com.example.haus.constant;

public class ErrorMessage {

    private ErrorMessage() {}

    public static final String ERR_EXCEPTION_GENERAL = "exception.general";
    public static final String UNAUTHORIZED = "exception.unauthorized";
    public static final String FORBIDDEN = "exception.forbidden";
    public static final String FORBIDDEN_UPDATE_DELETE = "exception.forbidden.update-delete";

    //error validation dto
    public static final String INVALID_SOME_THING_FIELD = "invalid.general";
    public static final String INVALID_FORMAT_SOME_THING_FIELD = "invalid.general.format";
    public static final String INVALID_SOME_THING_FIELD_IS_REQUIRED = "invalid.general.required";
    public static final String NOT_BLANK_FIELD = "invalid.general.not-blank";
    public static final String INVALID_FORMAT_PASSWORD = "invalid.password-format";
    public static final String INVALID_DATE = "invalid.date-format";
    public static final String INVALID_DATE_FEATURE = "invalid.date-future";
    public static final String INVALID_DATETIME = "invalid.datetime-format";

    public static class Validator {

        private Validator() {}

        public static final String ERR_INPUT_CONSTRAINT_VALIDATE = "exception.input.value.must.be.greater.than.or.equal.to.0.if.entry.data";
        public static final String ERR_PHONE_VALIDATOR = "exception.phone.not.matches.pattern";
        public static final String ERR_GENDER_VALIDATOR = "exception.gender.must.be.not.any.of.array.['male', 'female', 'other']";
        public static final String ERR_ENUM_VALUE_VALIDATOR = "exception.{name}.must.be.not.any.of.enum.{enumClass}";
        public static final String ERR_EMAIL_VALIDATOR = "exception.email.invalid";
    }

    public static class Auth {

        private Auth() {}

        public static final String ERR_INCORRECT_USERNAME = "exception.auth.incorrect.username";
        public static final String ERR_INCORRECT_PASSWORD = "exception.auth.incorrect.password";
        public static final String ERR_ACCOUNT_NOT_ENABLED = "exception.auth.account.not.enabled";
        public static final String ERR_ACCOUNT_LOCKED = "exception.auth.account.locked";
        public static final String INVALID_REFRESH_TOKEN = "exception.auth.invalid.refresh.token";
        public static final String EXPIRED_REFRESH_TOKEN = "exception.auth.expired.refresh.token";
        public static final String ERR_TOKEN_INVALIDATED = "exception.auth.token.invalidated";
        public static final String ERR_MALFORMED_TOKEN = "exception.auth.malformed.token";
        //OTP
        public static final String ERR_PENDING_RESET_REQUEST_NULL = "exception.auth.pending.reset.request.null";
        public static final String ERR_PENDING_REGISTER_REQUEST_NULL = "exception.auth.pending.register.request.null";
        public static final String ERR_OTP_EXPIRED = "exception.auth.otp.expired";
        public static final String ERR_OTP_NOT_MATCH = "exception.auth.otp.not.match";
    }

    public static class User {

        private User() {}

        public static final String ERR_USER_NOT_EXISTED = "exception.user.user.not.existed";
        public static final String ERR_USERNAME_EXISTED = "exception.user.username.existed";
        public static final String ERR_EMAIL_EXISTED = "exception.user.email.existed";
        public static final String ERR_EMAIL_NOT_EXISTED = "exception.user.email.not.existed";
        public static final String ERR_RE_ENTER_PASSWORD_NOT_MATCH = "exception.user.re-enter.password.not.match";
        public static final String ERR_DUPLICATE_OLD_PASSWORD = "exception.user.duplicate_old_password";
        public static final String ERR_PASSWORD_NOT_BLANK = "exception.user.password.confirm.not_blank";
        public static final String ERR_PHONE_EXISTED = "exception.user.phone.existed";
        public static final String ERR_USER_IS_LOCKED = "exception.user.is.locked";
        public static final String ERR_USER_IS_NOT_LOCKED = "exception.user.is.not.locked";
        public static final String ERR_ACCOUNT_ALREADY_DELETED = "exception.user.account.already.deleted";
        public static final String ERR_ACCOUNT_RECOVERY_EXPIRED = "exception.user.account.recovery.period.has.expired";
        public static final String ERR_ACCOUNT_NOT_DELETED = "exception.user.account.is.not.in.deleted.state";
        public static final String ERR_INCORRECT_PASSWORD = "exception.user.incorrect.password";
        public static final String ERR_PERSONAL_INFORMATION_NOT_COMPLETED = "exception.user.personal.information.not.completed";
        public static final String UPLOAD_AVATAR_FAIL = "exception.user.upload.fail";
    }

    public static class Category {

        private Category() {}

        public static final String ERR_CATEGORY_EXISTED = "exception.category.existed";
        public static final String ERR_CATEGORY_NOT_EXISTED = "exception.category.not.existed";
    }

    public static class Promotion {

        private Promotion() {}

        public static final String ERR_PROMOTION_EXISTED = "exception.promotion.existed";
        public static final String ERR_PROMOTION_NOT_EXISTED = "exception.promotion.not.existed";
        public static final String ERR_PROMOTION_CODE_NOT_BLANK = "exception.promotion.code.not.blank";
        public static final String ERR_PROMOTION_DESCRIPTION_NOT_NULL = "exception.promotion.description.not.null";
        public static final String ERR_PROMOTION_TYPE_NOT_BLANK = "exception.promotion.type.not.null";
        public static final String ERR_PROMOTION_STATUS_NOT_BLANK = "exception.promotion.status.not.null";
        public static final String ERR_PROMOTION_START_DATE_NOT_EMPTY = "exception.promotion.start.date.not.empty";
        public static final String ERR_PROMOTION_END_DATE_NOT_EMPTY = "exception.promotion.end.date.not.empty";
        public static final String ERR_PROMOTION_CODE_LENGTH = "exception.promotion.code.length.must.be.exactly.6.digits.long";
        public static final String ERR_PROMOTION_DISCOUNT_PERCENT_NOT_BLANK = "exception.promotion.discount.percent.must.be.not.blank";
        public static final String ERR_PROMOTION_DISCOUNT_PERCENT_MIN_VALIDATE = "exception.promotion.discount.percent.must.be.greater.than.or.equal.to.0";
        public static final String ERR_PROMOTION_DISCOUNT_PERCENT_MAX_VALIDATE = "exception.promotion.discount.percent.must.be.less.than.or.equal.to.110";
    }

    public static class Product {
        public static final String ERR_PRICE_INVALID = "exception.product.price.invalid";
        public static final String ERR_QUANTITY_INVALID = "exception.product.quantity.invalid";
        public static final String ERR_PRODUCT_NOT_EXISTED = "exception.product.not.existed";
        public static final String ERR_PRODUCT_NAME_EXISTED = "exception.product.name.existed";
        public static final String ERR_PRODUCT_ALREADY_DELETED = "exception.product.already.deleted";
        public static final String ERR_PRODUCT_VARIATION_NOT_EXISTED = "exception.product.variation.not.existed";
        public static final String ERR_PRODUCT_VARIATION_ALREADY_DELETED = "exception.product.variation.already.deleted";
    }
}
