import com.google.gson.annotations.SerializedName

data class CusEditMonitoring(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("data")
    val data: CustomerData? = null,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("errors")
    val errors: List<Any> = emptyList(),
    @SerializedName("pagination")
    val pagination: List<Any> = emptyList()
)

data class CustomerData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("uuid")
    val uuid: String = "",
    @SerializedName("organisation_id")
    val organisationId: Int = 0,
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("region_id")
    val regionId: Int = 0,
    @SerializedName("route_id")
    val routeId: Int? = null,
    @SerializedName("is_parent")
    val isParent: Int = 0,
    @SerializedName("payment_term_id")
    val paymentTermId: Int? = null,
    @SerializedName("customer_group_id")
    val customerGroupId: Int? = null,
    @SerializedName("sales_organisation_id")
    val salesOrganisationId: Int = 0,
    @SerializedName("channel_id")
    val channelId: Int = 0,
    @SerializedName("sub_secondary_channel_id")
    val subSecondaryChannelId: Int = 0,
    @SerializedName("customer_category_id")
    val customerCategoryId: Int = 0,
    @SerializedName("merchandiser_id")
    val merchandiserId: Int? = null,
    @SerializedName("customer_code")
    val customerCode: String = "",
    @SerializedName("erp_code")
    val erpCode: String? = null,
    @SerializedName("customer_type_id")
    val customerTypeId: Int = 0,
    @SerializedName("no_of_invoice")
    val noOfInvoice: Int = 0,
    @SerializedName("radius")
    val radius: String = "",
    @SerializedName("customer_address_1")
    val customerAddress1: String = "",
    @SerializedName("customer_address_2")
    val customerAddress2: String = "",
    @SerializedName("trn_no")
    val trnNo: String = "",
    @SerializedName("trn_name")
    val trnName: String = "",
    @SerializedName("customer_city")
    val customerCity: String = "",
    @SerializedName("customer_state")
    val customerState: String? = null,
    @SerializedName("customer_zipcode")
    val customerZipcode: String = "",
    @SerializedName("customer_phone")
    val customerPhone: String = "",
    @SerializedName("customer_address_1_lat")
    val customerAddress1Lat: String = "",
    @SerializedName("customer_address_1_lang")
    val customerAddress1Lang: String = "",
    @SerializedName("customer_address_2_lat")
    val customerAddress2Lat: String = "",
    @SerializedName("customer_address_2_lang")
    val customerAddress2Lang: String = "",
    @SerializedName("balance")
    val balance: String = "",
    @SerializedName("credit_limit")
    val creditLimit: String = "",
    @SerializedName("credit_days")
    val creditDays: Int = 0,
    @SerializedName("ship_to_party")
    val shipToParty: ShipToPartyData? = null,
    @SerializedName("sold_to_party")
    val soldToParty: SoldToPartyData? = null,
    @SerializedName("payer")
    val payer: PayerData? = null,
    @SerializedName("bill_to_payer")
    val billToPayer: BillToPayerData? = null,
    @SerializedName("profile_image")
    val profileImage: String = "",
    @SerializedName("expired_date")
    val expiredDate: String? = null,
    @SerializedName("lop")
    val lop: String? = null,
    @SerializedName("due_on")
    val dueOn: String = "",
    @SerializedName("source")
    val source: Int = 0,
    @SerializedName("current_stage")
    val currentStage: String = "",
    @SerializedName("current_stage_comment")
    val currentStageComment: String? = null,
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("is_lob")
    val isLob: Int = 0,
    @SerializedName("amount")
    val amount: String? = null,
    @SerializedName("rebate")
    val rebate: Int = 0,
    @SerializedName("sales_enable")
    val salesEnable: String = "",
    @SerializedName("return_enable")
    val returnEnable: String = "",
    @SerializedName("collection_enable")
    val collectionEnable: String = "",
    @SerializedName("print_enable")
    val printEnable: String = "",
    @SerializedName("geo_checking")
    val geoChecking: String = "",
    @SerializedName("credit_limit_check")
    val creditLimitCheck: String = "",
    @SerializedName("block_customer")
    val blockCustomer: String = "",
    @SerializedName("promotion_enable")
    val promotionEnable: String = "",
    @SerializedName("stock_capture")
    val stockCapture: String = "",
    @SerializedName("discount")
    val discount: String = "",
    @SerializedName("barcode_print")
    val barcodePrint: String = "",
    @SerializedName("service_start_time")
    val serviceStartTime: String = "",
    @SerializedName("service_end_time")
    val serviceEndTime: String = "",
    @SerializedName("total_window_time")
    val totalWindowTime: String? = null,
    @SerializedName("contact_person")
    val contactPerson: String? = null,
    @SerializedName("street")
    val street: String? = null,
    @SerializedName("location")
    val location: String = "",
    @SerializedName("fax_no")
    val faxNo: String? = null,
    @SerializedName("sponsor_name")
    val sponsorName: String? = null,
    @SerializedName("sponsor_nationality")
    val sponsorNationality: String? = null,
    @SerializedName("sponsor_contact_no")
    val sponsorContactNo: String? = null,
    @SerializedName("proprietor_name")
    val proprietorName: String? = null,
    @SerializedName("proprietor_nationality")
    val proprietorNationality: String? = null,
    @SerializedName("proprietor_contact_no")
    val proprietorContactNo: String? = null,
    @SerializedName("owner_visa_passport")
    val ownerVisaPassport: String = "",
    @SerializedName("sponsor_visa_passport")
    val sponsorVisaPassport: String = "",
    @SerializedName("sponsor_region_id")
    val sponsorRegionId: String? = null,
    @SerializedName("fta_registration_copy")
    val ftaRegistrationCopy: String? = null,
    @SerializedName("nature_of_business")
    val natureOfBusiness: String? = null,
    @SerializedName("year_of_establishment")
    val yearOfEstablishment: String? = null,
    @SerializedName("trade_license")
    val tradeLicense: String = "",
    @SerializedName("trade_license_copy")
    val tradeLicenseCopy: String = "",
    @SerializedName("trade_expiry_date")
    val tradeExpiryDate: String? = null,
    @SerializedName("chamber_of_commerce")
    val chamberOfCommerce: String? = null,
    @SerializedName("expiry_date")
    val expiryDate: String? = null,
    @SerializedName("bank_informantion")
    val bankInformantion: String? = null,
    @SerializedName("branch_associates_name")
    val branchAssociatesName: String? = null,
    @SerializedName("branch_associates_address")
    val branchAssociatesAddress: String? = null,
    @SerializedName("delivery_time_window")
    val deliveryTimeWindow: String? = null,
    @SerializedName("estimate_monthly_purchase")
    val estimateMonthlyPurchase: String = "",
    @SerializedName("estimate_qty_amt")
    val estimateQtyAmt: String = "",
    @SerializedName("bank_guarrtee_value")
    val bankGuarrteeValue: String = "",
    @SerializedName("estimate_expiry_date")
    val estimateExpiryDate: String = "",
    @SerializedName("name_of_good_recevier")
    val nameOfGoodRecevier: String = "",
    @SerializedName("owner_passport_copy_expiry_date")
    val ownerPassportCopyExpiryDate: String = "",
    @SerializedName("sponser_passport_copy_expiry_date")
    val sponserPassportCopyExpiryDate: String = "",
    @SerializedName("trade_lince_expiry_date")
    val tradeLinceExpiryDate: String = "",
    @SerializedName("account_number_bank")
    val accountNumberBank: String = "",
    @SerializedName("other_image")
    val otherImage: String = "",
    @SerializedName("sales_geo_checkin")
    val salesGeoCheckin: String = "",
    @SerializedName("return_geo_checkin")
    val returnGeoCheckin: String = "",
    @SerializedName("collection_geo_checkin")
    val collectionGeoCheckin: String = "",
    @SerializedName("capture_location_enable")
    val captureLocationEnable: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("deleted_at")
    val deletedAt: String? = null,
    @SerializedName("user")
    val user: UserData? = null,
    @SerializedName("customer_merchandiser")
    val customerMerchandiser: List<Any> = emptyList(),
    @SerializedName("addresses")
    val addresses: List<Any> = emptyList(),
    @SerializedName("channel")
    val channel: ChannelData? = null,
    @SerializedName("region")
    val region: RegionData? = null,
    @SerializedName("customer_group")
    val customerGroup: Any? = null,
    @SerializedName("customer_category")
    val customerCategory: CustomerCategoryData? = null,
    @SerializedName("customer_type")
    val customerType: CustomerTypeData? = null,
    @SerializedName("sales_organisation")
    val salesOrganisation: SalesOrganisationData? = null,
    @SerializedName("payment_term")
    val paymentTerm: Any? = null,
    @SerializedName("custom_field_value_save")
    val customFieldValueSave: List<Any> = emptyList(),
    @SerializedName("customer_block_types")
    val customerBlockTypes: List<Any> = emptyList(),
    @SerializedName("customerlob")
    val customerlob: List<Any> = emptyList(),
    @SerializedName("customer_document")
    val customerDocument: List<Any> = emptyList(),
    @SerializedName("customer_delivery_day")
    val customerDeliveryDay: List<CustomerDeliveryDayData> = emptyList(),
    @SerializedName("sub_secondary_channel")
    val subSecondaryChannel: SubSecondaryChannelData? = null
)

data class ShipToPartyData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("customer_code")
    val customerCode: String = "",
    @SerializedName("user")
    val user: UserData
)

data class SoldToPartyData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("customer_code")
    val customerCode: String = "",
    @SerializedName("user")
    val user: UserData
)

data class PayerData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("customer_code")
    val customerCode: String = "",
    @SerializedName("user")
    val user: UserData
)

data class BillToPayerData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("customer_code")
    val customerCode: String = "",
    @SerializedName("user")
    val user: UserData
)

data class UserData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("usertype")
    val usertype: Int = 0,
    @SerializedName("firstname")
    val firstname: String = "",
    @SerializedName("lastname")
    val lastname: String = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("mobile")
    val mobile: Any? = null,
    @SerializedName("role_id")
    val roleId: Int = 0,
    @SerializedName("country_id")
    val countryId: Int = 0,
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("parent_id")
    val parentId: String = "",
    @SerializedName("country")
    val country: CountryData
)

data class CountryData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = ""
)

data class ChannelData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("status")
    val status: Int = 0
)

data class RegionData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("region_name")
    val regionName: String = "",
    @SerializedName("region_status")
    val regionStatus: Int = 0
)

data class CustomerCategoryData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("customer_category_code")
    val customerCategoryCode: String = "",
    @SerializedName("customer_category_name")
    val customerCategoryName: String = ""
)

data class CustomerTypeData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("customer_type_name")
    val customerTypeName: String = ""
)

data class SalesOrganisationData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = ""
)

data class CustomerDeliveryDayData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("organisation_id")
    val organisationId: Int = 0,
    @SerializedName("uuid")
    val uuid: String = "",
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("day")
    val day: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("deleted_at")
    val deletedAt: String? = null
)

data class SubSecondaryChannelData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("uuid")
    val uuid: String = "",
    @SerializedName("organisation_id")
    val organisationId: Int = 0,
    @SerializedName("parent_id")
    val parentId: Any? = null,
    @SerializedName("sub_secondary_channele_name")
    val subSecondaryChanneleName: String = "",
    @SerializedName("node_level")
    val nodeLevel: Int = 0,
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("deleted_at")
    val deletedAt: Any? = null
)
