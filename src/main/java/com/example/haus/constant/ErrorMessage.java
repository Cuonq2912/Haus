package com.example.haus.constant;

public class ErrorMessage {

    private ErrorMessage() {
    }

    public static final String ERR_EXCEPTION_GENERAL = "exception.general";
    public static final String UNAUTHORIZED = "exception.unauthorized";
    public static final String FORBIDDEN = "exception.forbidden";
    public static final String FORBIDDEN_UPDATE_DELETE = "exception.forbidden.update-delete";

    // error validation dto
    public static final String INVALID_SOME_THING_FIELD = "invalid.general";
    public static final String INVALID_FORMAT_SOME_THING_FIELD = "invalid.general.format";
    public static final String INVALID_SOME_THING_FIELD_IS_REQUIRED = "invalid.general.required";
    public static final String NOT_BLANK_FIELD = "invalid.general.not-blank";
    public static final String NOT_EMPTY_FIELD = "invalid.general.not-empty";
    public static final String NOT_NULL_FIELD = "invalid.general.not-null";
    public static final String INVALID_FORMAT_PASSWORD = "invalid.password-format";
    public static final String INVALID_DATE = "invalid.date-format";
    public static final String INVALID_DATE_FEATURE = "invalid.date-future";
    public static final String INVALID_DATETIME = "invalid.datetime-format";
    public static final String MUST_BE_POSITIVE = "invalid.general.must.positive";

    public static class Validator {

        private Validator() {
        }

        public static final String ERR_INPUT_CONSTRAINT_VALIDATE = "exception.input.value.must.be.greater.than.or.equal.to.0.if.entry.data.AND.min.less.than.max.price";
        public static final String ERR_PHONE_VALIDATOR = "exception.phone.not.matches.pattern";
        public static final String ERR_GENDER_VALIDATOR = "exception.gender.must.be.not.any.of.array.['male', 'female', 'other']";
        public static final String ERR_ENUM_VALUE_VALIDATOR = "exception.{name}.must.be.not.any.of.enum.{enumClass}";
        public static final String ERR_EMAIL_VALIDATOR = "exception.email.invalid";
    }

    public static class Auth {

        private Auth() {
        }

        public static final String ERR_INCORRECT_USERNAME = "exception.auth.incorrect.username";
        public static final String ERR_INCORRECT_PASSWORD = "exception.auth.incorrect.password";
        public static final String ERR_ACCOUNT_NOT_ENABLED = "exception.auth.account.not.enabled";
        public static final String ERR_ACCOUNT_LOCKED = "exception.auth.account.locked";
        public static final String INVALID_REFRESH_TOKEN = "exception.auth.invalid.refresh.token";
        public static final String EXPIRED_REFRESH_TOKEN = "exception.auth.expired.refresh.token";
        public static final String ERR_TOKEN_INVALIDATED = "exception.auth.token.invalidated";
        public static final String ERR_MALFORMED_TOKEN = "exception.auth.malformed.token";
        public static final String ERR_CAN_NOT_CREATE_USER = "exception.can.not.create.user";
        public static final String ERR_CAN_NOT_SEND_RESET_PASSWORD_EMAIL = "exception.can.not.send.reset.password.email";
        public static final String ERR_RESET_PASSWORD_FAILED_IN_KEYCLOAK = "exception.reset.password.failed.in.keycloak";
        public static final String ERR_REFRESH_TOKEN_FAILED_IN_KEYCLOAK = "exception.refresh.token.failed.in.keycloak";
        // OTP
        public static final String ERR_PENDING_RESET_REQUEST_NULL = "exception.auth.pending.reset.request.null";
        public static final String ERR_PENDING_REGISTER_REQUEST_NULL = "exception.auth.pending.register.request.null";
        public static final String ERR_OTP_EXPIRED = "exception.auth.otp.expired";
        public static final String ERR_OTP_NOT_MATCH = "exception.auth.otp.not.match";
        public static final String ERR_VERIFY_FAILED_IN_KEYCLOAK = "exception.verify.failed.in.keycloak";
        public static final String ERR_LOGIN_FAILED_IN_KEYCLOAK = "exception.login.failed.in.keycloak";
        public static final String ERR_USERNAME_PASSWORD_INCORRECT = "exception.username.password.incorrect";
    }

    public static class User {

        private User() {
        }

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

        private Category() {
        }

        public static final String ERR_CATEGORY_EXISTED = "exception.category.existed";
        public static final String ERR_CATEGORY_NOT_EXISTED = "exception.category.not.existed";
        public static final String ERR_CATEGORY_BEING_USED = "exception.category.being.used.cannot.delete.because.include.product";
    }

    public static class Promotion {

        private Promotion() {
        }

        public static final String ERR_PROMOTION_EXISTED = "exception.promotion.existed";
        public static final String ERR_PROMOTION_NOT_EXISTED = "exception.promotion.not.existed";
        public static final String ERR_PROMOTION_CODE_NOT_BLANK = "exception.promotion.code.not.blank";
        public static final String ERR_PROMOTION_DESCRIPTION_NOT_NULL = "exception.promotion.description.not.null";
        public static final String ERR_PROMOTION_TYPE_NOT_NULL = "exception.promotion.type.not.null";
        public static final String ERR_PROMOTION_STATUS_NOT_NULL = "exception.promotion.status.not.null";
        public static final String ERR_PROMOTION_START_DATE_NOT_NULL = "exception.promotion.start.date.not.empty";
        public static final String ERR_PROMOTION_END_DATE_NOT_NULL = "exception.promotion.end.date.not.empty";
        public static final String ERR_PROMOTION_CODE_LENGTH = "exception.promotion.code.length.must.be.exactly.in.6.to.15";
        public static final String ERR_PROMOTION_DISCOUNT_PERCENT_NOT_NULL = "exception.promotion.discount.percent.must.be.not.blank";
        public static final String ERR_PROMOTION_DISCOUNT_PERCENT_INVALID = "exception.promotion.discount.percent.must.be.in.1..100";
        public static final String ERR_PROMOTION_EXPIRED = "exception.promotion.expired";
        public static final String ERR_PROMOTION_INACTIVE = "exception.promotion.expired";
    }

    public static class Product {

        private Product() {
        }

        public static final String ERR_PRICE_INVALID = "exception.product.price.invalid";
        public static final String ERR_QUANTITY_INVALID = "exception.product.quantity.invalid";
        public static final String ERR_PRODUCT_NOT_EXISTED = "exception.product.not.existed";
        public static final String ERR_PRODUCT_NAME_EXISTED = "exception.product.name.existed";
        public static final String ERR_PRODUCT_ALREADY_DELETED = "exception.product.already.deleted";
        public static final String ERR_PRODUCT_VARIATION_NOT_EXISTED = "exception.product.variation.not.existed";
        public static final String ERR_PRODUCT_VARIATION_ALREADY_DELETED = "exception.product.variation.already.deleted";
        public static final String ERR_PRODUCT_INVENTORY_QUANTITY_LESS_THAN_1 = "exception.product.inventory.quantity.less.than.1";
    }

    public static class Order {

        private Order() {
        }

        public static final String ERR_PAYMENT_NOT_FOUND = "exception.order.not.found";
        public static final String ERR_ORDER_NOT_EXISTED = "exception.order.not.existed";
        public static final String ERR_PAYMENT_TYPE_INVALID = "exception.order.payment.type.invalid";
        public static final String ERR_PAYMENT_EXPIRED = "exception.order.payment.expired";
        public static final String ERR_PAYMENT_STATUS_INVALID = "exception.order.payment.status.invalid";
        public static final String ERR_PAYMENT_COMPLETED = "exception.order.payment.completed";
        public static final String ERR_ORDER_NOT_COMPLETED = "exception.order.not.completed";
        public static final String ERR_INVALID_ORDER_STATUS = "exception.order.status.incompatible";
        public static final String ERR_ORDER_ITEMS_EMPTY = "exception.order.items.empty";

        //request
        public static final String ERR_PRODUCTS_EMPTY = "exception.products.empty";
        public static final String ERR_ORDER_NUMBER_EMPTY = "exception.order.number.empty";
        public static final String ERR_SHIPPING_FEE_EMPTY = "exception.shipping.fee.empty";
        public static final String ERR_TOTAL_AMOUNT_EMPTY = "exception.total.amount.empty";
        public static final String ERR_ORDER_DATE_EMPTY = "exception.order.date.empty";
        public static final String ERR_ADDRESS_ID_EMPTY = "exception.address.id.empty";
        public static final String ERR_PROMOTION_ID_EMPTY = "exception.promotion.id.empty";
        public static final String ERR_PAYMENT_TYPE_EMPTY = "exception.promotion.type.empty";
        public static final String ERR_PRODUCT_VARIATION_ID_EMPTY = "exception.product.variation.id.empty";
        public static final String ERR_QUANTITY_EMPTY = "exception.quantity.empty";
        public static final String ERR_PRICE_AT_SALE_EMPTY = "exception.price.at.sale.empty";
        public static final String ERR_NUMBER_INVALID = "exception.number.invalid";

    }

    public static class Payment {

        private Payment() {
        }

        public static final String CALLBACK_VNPAY_FAIL = "exception.vnpay.callback.fail";
        public static final String IPN_VERIFY_FAIL = "exception.vnpay.ipn.verify.fail";
        public static final String CREATE_VNPAY_URL_FAIL = "exception.vnpay.url.create.fail";
        public static final String ERR_ORDER_TOTAL_AMOUNT_NOT_MATCH = "exception.order.total.amount.not.match";

        public static final String CREATE_MOMO_ORDER_FAIL = "exception.momo.create.order.fail";
        public static final String MOMO_IPN_VERIFY_FAIL = "exception.momo.ipn.verify.fail";
        public static final String MOMO_CALLBACK_FAIL = "exception.momo.callback.fail";
        public static final String MOMO_AMOUNT_TOO_LOW = "exception.momo.amount.too.low";
        public static final String MOMO_AMOUNT_TOO_HIGH = "exception.momo.amount.too.high";
        public static final String MOMO_ORDER_ALREADY_PAID = "exception.momo.order.already.paid";
        public static final String MOMO_ORDER_CANCELLED = "exception.momo.order.cancelled";
        public static final String MOMO_PAYMENT_PENDING = "exception.momo.payment.pending";
        public static final String MOMO_PAYMENT_COMPLETED = "exception.momo.payment.completed";

        public static final String COD_ORDER_ALREADY_COMPLETED = "exception.cod.order.already.completed";
        public static final String COD_ORDER_CANCELLED = "exception.cod.order.cancelled";
        public static final String COD_PAYMENT_ALREADY_COMPLETED = "exception.cod.payment.already.completed";
        public static final String STATUS_IS_NOT_SUPPORT = "exception.status.is.not.support";


    }

    public static class Cart {
        private Cart() {}

        public static final String ERR_CART_NOT_FOUND = "exception.cart.not.found";
        public static final String ERR_CART_QUANTITY_INVALID = "exception.cart.quantity.invalid";
        public static final String ERR_CART_ITEM_NOT_EXISTED_IN_CART = "exception.cart.item.not.existed.in.cart";
        public static final String ERR_CURR_CART_ITEM_NOT_EXISTED_IN_CART = "exception.curr.cart.item.not.existed.in.cart";
    }

    public static class Address {

        private Address() {}

        public static final String ERR_RECIPIENT_NAME_EMPTY = "exception.recipient.name.empty";
        public static final String ERR_PHONE_NUMBER_EMPTY = "exception.phone.number.empty";
        public static final String ERR_COUNTRY_EMPTY = "exception.country.empty";
        public static final String ERR_CITY_EMPTY = "exception.city.empty";
        public static final String ERR_DISTRICT_EMPTY = "exception.district.empty";
        public static final String ERR_COMMUNE_EMPTY = "exception.commune.empty";
        public static final String ERR_DETAIL_ADDRESS_NULL = "exception.detail.address.null";
        public static final String ERR_ADDRESS_NOT_FOUND = "exception.address.not.found";
        public static final String ERR_ID_EMPTY = "exception.id.found";
        public static final String ERR_IS_SELECTED_NULL = "exception.is.selected.found";
    }

}
