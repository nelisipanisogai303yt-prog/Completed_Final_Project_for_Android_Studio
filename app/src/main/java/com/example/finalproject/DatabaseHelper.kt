package com.example.finalproject

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "BiteDash.db"
        private const val DATABASE_VERSION = 7

        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_ROLE = "role"
        private const val COLUMN_ADDRESS = "address"
        private const val COLUMN_WALLET = "wallet_balance"

        private const val TABLE_STORES = "stores"
        private const val COLUMN_STORE_ID = "store_id"
        private const val COLUMN_STORE_NAME = "store_name"
        private const val COLUMN_STORE_CATEGORY = "category"
        private const val COLUMN_STORE_RATING = "rating"
        private const val COLUMN_STORE_TIME = "delivery_time_mins"
        private const val COLUMN_STORE_FEE = "delivery_fee"
        private const val COLUMN_STORE_DISTANCE = "distance_km"
        private const val COLUMN_STORE_IMAGE = "image_type"

        private const val TABLE_MENU = "menu_items"
        private const val COLUMN_ITEM_ID = "item_id"
        private const val COLUMN_MENU_STORE = "store_name"
        private const val COLUMN_ITEM_NAME = "item_name"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_MENU_CATEGORY = "category"
        private const val COLUMN_ITEM_IMAGE = "image_type"
        private const val COLUMN_ITEM_RATING = "rating"
        private const val COLUMN_ITEM_PREP = "prep_time_mins"

        private const val TABLE_ORDERS = "orders"
        private const val COLUMN_ORDER_ID = "order_id"
        private const val COLUMN_CUSTOMER_NAME = "customer_name"
        private const val COLUMN_ORDER_STORE = "store_name"
        private const val COLUMN_ORDER_ITEM = "item_name"
        private const val COLUMN_PAYMENT_TYPE = "payment_type"
        private const val COLUMN_TOTAL_PRICE = "total_price"
        private const val COLUMN_SPECIAL_NOTE = "special_note"
        private const val COLUMN_ORDER_STATUS = "status"
        private const val COLUMN_ORDER_RATING = "rating"
        private const val COLUMN_ORDER_REVIEW = "review"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USERNAME TEXT UNIQUE, "
                + "$COLUMN_PASSWORD TEXT, "
                + "$COLUMN_ROLE TEXT, "
                + "$COLUMN_ADDRESS TEXT, "
                + "$COLUMN_WALLET REAL)")

        val createStoresTable = ("CREATE TABLE $TABLE_STORES ("
                + "$COLUMN_STORE_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_STORE_NAME TEXT UNIQUE, "
                + "$COLUMN_STORE_CATEGORY TEXT, "
                + "$COLUMN_STORE_RATING REAL, "
                + "$COLUMN_STORE_TIME INTEGER, "
                + "$COLUMN_STORE_FEE REAL, "
                + "$COLUMN_STORE_DISTANCE REAL, "
                + "$COLUMN_STORE_IMAGE TEXT)")

        val createMenuTable = ("CREATE TABLE $TABLE_MENU ("
                + "$COLUMN_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_MENU_STORE TEXT, "
                + "$COLUMN_ITEM_NAME TEXT, "
                + "$COLUMN_DESCRIPTION TEXT, "
                + "$COLUMN_PRICE REAL, "
                + "$COLUMN_MENU_CATEGORY TEXT, "
                + "$COLUMN_ITEM_IMAGE TEXT, "
                + "$COLUMN_ITEM_RATING REAL, "
                + "$COLUMN_ITEM_PREP INTEGER)")

        val createOrdersTable = ("CREATE TABLE $TABLE_ORDERS ("
                + "$COLUMN_ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_CUSTOMER_NAME TEXT, "
                + "$COLUMN_ORDER_STORE TEXT, "
                + "$COLUMN_ORDER_ITEM TEXT, "
                + "$COLUMN_PAYMENT_TYPE TEXT, "
                + "$COLUMN_TOTAL_PRICE REAL, "
                + "$COLUMN_SPECIAL_NOTE TEXT, "
                + "$COLUMN_ORDER_STATUS TEXT, "
                + "$COLUMN_ORDER_RATING INTEGER, "
                + "$COLUMN_ORDER_REVIEW TEXT)")

        db.execSQL(createUsersTable)
        db.execSQL(createStoresTable)
        db.execSQL(createMenuTable)
        db.execSQL(createOrdersTable)

        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STORES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MENU")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        onCreate(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        val defaultCustomer = ContentValues().apply {
            put(COLUMN_USERNAME, "customer")
            put(COLUMN_PASSWORD, "password123")
            put(COLUMN_ROLE, "Customer")
            put(COLUMN_ADDRESS, "123 University Belt, Sampaloc, Manila")
            put(COLUMN_WALLET, 1500.00)
        }
        val defaultOwner = ContentValues().apply {
            put(COLUMN_USERNAME, "owner")
            put(COLUMN_PASSWORD, "password123")
            put(COLUMN_ROLE, "Store Owner")
            put(COLUMN_ADDRESS, "BiteDash Express Kitchen HQ, Manila")
            put(COLUMN_WALLET, 5000.00)
        }
        db.insertWithOnConflict(TABLE_USERS, null, defaultCustomer, SQLiteDatabase.CONFLICT_IGNORE)
        db.insertWithOnConflict(TABLE_USERS, null, defaultOwner, SQLiteDatabase.CONFLICT_IGNORE)

        val stores = listOf(
            ContentValues().apply {
                put(COLUMN_STORE_NAME, "BiteDash Express Kitchen")
                put(COLUMN_STORE_CATEGORY, "Burgers & Combos")
                put(COLUMN_STORE_RATING, 4.9)
                put(COLUMN_STORE_TIME, 15)
                put(COLUMN_STORE_FEE, 25.00)
                put(COLUMN_STORE_DISTANCE, 1.2)
                put(COLUMN_STORE_IMAGE, "burger")
            },
            ContentValues().apply {
                put(COLUMN_STORE_NAME, "Krusty Crispy Chicken")
                put(COLUMN_STORE_CATEGORY, "Fried Chicken & Wings")
                put(COLUMN_STORE_RATING, 4.9)
                put(COLUMN_STORE_TIME, 18)
                put(COLUMN_STORE_FEE, 29.00)
                put(COLUMN_STORE_DISTANCE, 1.5)
                put(COLUMN_STORE_IMAGE, "chicken")
            },
            ContentValues().apply {
                put(COLUMN_STORE_NAME, "Daily Roast Coffee Cafe")
                put(COLUMN_STORE_CATEGORY, "Coffee & Espresso")
                put(COLUMN_STORE_RATING, 4.9)
                put(COLUMN_STORE_TIME, 10)
                put(COLUMN_STORE_FEE, 19.00)
                put(COLUMN_STORE_DISTANCE, 0.7)
                put(COLUMN_STORE_IMAGE, "coffee")
            },
            ContentValues().apply {
                put(COLUMN_STORE_NAME, "Boba & Tea House")
                put(COLUMN_STORE_CATEGORY, "Milk Tea & Fruit Tea")
                put(COLUMN_STORE_RATING, 4.9)
                put(COLUMN_STORE_TIME, 12)
                put(COLUMN_STORE_FEE, 19.00)
                put(COLUMN_STORE_DISTANCE, 0.8)
                put(COLUMN_STORE_IMAGE, "drink")
            },
            ContentValues().apply {
                put(COLUMN_STORE_NAME, "Pizza Kingdom PH")
                put(COLUMN_STORE_CATEGORY, "Pizza & Italian")
                put(COLUMN_STORE_RATING, 4.8)
                put(COLUMN_STORE_TIME, 25)
                put(COLUMN_STORE_FEE, 49.00)
                put(COLUMN_STORE_DISTANCE, 3.5)
                put(COLUMN_STORE_IMAGE, "pizza")
            },
            ContentValues().apply {
                put(COLUMN_STORE_NAME, "Snack Attack PH")
                put(COLUMN_STORE_CATEGORY, "Fries & Nachos")
                put(COLUMN_STORE_RATING, 4.7)
                put(COLUMN_STORE_TIME, 12)
                put(COLUMN_STORE_FEE, 15.00)
                put(COLUMN_STORE_DISTANCE, 0.9)
                put(COLUMN_STORE_IMAGE, "snack")
            },
            ContentValues().apply {
                put(COLUMN_STORE_NAME, "Sizzling Rice Bowls")
                put(COLUMN_STORE_CATEGORY, "Lunch & Dinner Sizzling")
                put(COLUMN_STORE_RATING, 4.8)
                put(COLUMN_STORE_TIME, 20)
                put(COLUMN_STORE_FEE, 29.00)
                put(COLUMN_STORE_DISTANCE, 2.1)
                put(COLUMN_STORE_IMAGE, "combo")
            }
        )

        for (store in stores) {
            db.insertWithOnConflict(TABLE_STORES, null, store, SQLiteDatabase.CONFLICT_IGNORE)
        }

        val menuItems = listOf(
            ContentValues().apply {
                put(COLUMN_MENU_STORE, "Krusty Crispy Chicken")
                put(COLUMN_ITEM_NAME, "2pc Crispy Fried Chicken Meal")
                put(COLUMN_DESCRIPTION, "Golden crunchy fried chicken served with hot rice & gravy.")
                put(COLUMN_PRICE, 179.00)
                put(COLUMN_MENU_CATEGORY, "Chicken")
                put(COLUMN_ITEM_IMAGE, "chicken")
                put(COLUMN_ITEM_RATING, 4.9)
                put(COLUMN_ITEM_PREP, 15)
            },
            ContentValues().apply {
                put(COLUMN_MENU_STORE, "Krusty Crispy Chicken")
                put(COLUMN_ITEM_NAME, "Buffalo Chicken Wings 6pcs")
                put(COLUMN_DESCRIPTION, "Spicy glazed buffalo wings served with ranch dip & fries.")
                put(COLUMN_PRICE, 229.00)
                put(COLUMN_MENU_CATEGORY, "Chicken")
                put(COLUMN_ITEM_IMAGE, "chicken")
                put(COLUMN_ITEM_RATING, 4.8)
                put(COLUMN_ITEM_PREP, 18)
            },
            ContentValues().apply {
                put(COLUMN_MENU_STORE, "Daily Roast Coffee Cafe")
                put(COLUMN_ITEM_NAME, "Iced Caramel Macchiato 16oz")
                put(COLUMN_DESCRIPTION, "Fresh espresso brewed with velvety milk & rich caramel drizzle.")
                put(COLUMN_PRICE, 135.00)
                put(COLUMN_MENU_CATEGORY, "Coffee")
                put(COLUMN_ITEM_IMAGE, "coffee")
                put(COLUMN_ITEM_RATING, 4.9)
                put(COLUMN_ITEM_PREP, 8)
            },
            ContentValues().apply {
                put(COLUMN_MENU_STORE, "Daily Roast Coffee Cafe")
                put(COLUMN_ITEM_NAME, "Spanish Latte Cream 16oz")
                put(COLUMN_DESCRIPTION, "Rich espresso with condensed milk and creamy foam.")
                put(COLUMN_PRICE, 140.00)
                put(COLUMN_MENU_CATEGORY, "Coffee")
                put(COLUMN_ITEM_IMAGE, "coffee")
                put(COLUMN_ITEM_RATING, 4.8)
                put(COLUMN_ITEM_PREP, 8)
            },
            ContentValues().apply {
                put(COLUMN_MENU_STORE, "Boba & Tea House")
                put(COLUMN_ITEM_NAME, "Wintermelon Milk Tea")
                put(COLUMN_DESCRIPTION, "Rich milk tea brewed with wintermelon syrup & chewy boba pearls.")
                put(COLUMN_PRICE, 110.00)
                put(COLUMN_MENU_CATEGORY, "Drinks")
                put(COLUMN_ITEM_IMAGE, "drink")
                put(COLUMN_ITEM_RATING, 4.9)
                put(COLUMN_ITEM_PREP, 8)
            },
            ContentValues().apply {
                put(COLUMN_MENU_STORE, "Boba & Tea House")
                put(COLUMN_ITEM_NAME, "Taro Cream Cheese Milk Tea")
                put(COLUMN_DESCRIPTION, "Creamy taro flavor topped with salted cream cheese cap.")
                put(COLUMN_PRICE, 125.00)
                put(COLUMN_MENU_CATEGORY, "Drinks")
                put(COLUMN_ITEM_IMAGE, "drink")
                put(COLUMN_ITEM_RATING, 4.8)
                put(COLUMN_ITEM_PREP, 8)
            },
            ContentValues().apply {
                put(COLUMN_MENU_STORE, "Snack Attack PH")
                put(COLUMN_ITEM_NAME, "Overloaded Cheese Fries & Nachos")
                put(COLUMN_DESCRIPTION, "Crispy fries & tortilla chips drizzled with melted cheese & bacon bits.")
                put(COLUMN_PRICE, 120.00)
                put(COLUMN_MENU_CATEGORY, "Snacks")
                put(COLUMN_ITEM_IMAGE, "snack")
                put(COLUMN_ITEM_RATING, 4.8)
                put(COLUMN_ITEM_PREP, 10)
            },
            ContentValues().apply {
                put(COLUMN_MENU_STORE, "Sizzling Rice Bowls")
                put(COLUMN_ITEM_NAME, "Sizzling Pork Sisig Rice Meal")
                put(COLUMN_DESCRIPTION, "Crispy chopped pork sisig served on a hot plate with fried egg & rice.")
                put(COLUMN_PRICE, 179.00)
                put(COLUMN_MENU_CATEGORY, "Rice Meals")
                put(COLUMN_ITEM_IMAGE, "combo")
                put(COLUMN_ITEM_RATING, 4.9)
                put(COLUMN_ITEM_PREP, 15)
            },
            ContentValues().apply {
                put(COLUMN_MENU_STORE, "BiteDash Express Kitchen")
                put(COLUMN_ITEM_NAME, "Double Bacon Stack Burger")
                put(COLUMN_DESCRIPTION, "Double beef patties, crispy bacon strips, smoked cheese & special sauce.")
                put(COLUMN_PRICE, 219.00)
                put(COLUMN_MENU_CATEGORY, "Burgers")
                put(COLUMN_ITEM_IMAGE, "burger")
                put(COLUMN_ITEM_RATING, 4.9)
                put(COLUMN_ITEM_PREP, 15)
            },
            ContentValues().apply {
                put(COLUMN_MENU_STORE, "Pizza Kingdom PH")
                put(COLUMN_ITEM_NAME, "Super Pepperoni Feast Pizza")
                put(COLUMN_DESCRIPTION, "Loaded with mozzarella cheese and double pepperoni slices.")
                put(COLUMN_PRICE, 349.00)
                put(COLUMN_MENU_CATEGORY, "Pizza")
                put(COLUMN_ITEM_IMAGE, "pizza")
                put(COLUMN_ITEM_RATING, 4.9)
                put(COLUMN_ITEM_PREP, 25)
            }
        )

        for (item in menuItems) {
            db.insert(TABLE_MENU, null, item)
        }
    }

    fun registerUser(username: String, password: String, role: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_ROLE, role)
            put(COLUMN_ADDRESS, "123 University Belt, Sampaloc, Manila")
            put(COLUMN_WALLET, 1000.00)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun getUserProfile(username: String): UserProfile? {
        val db = this.readableDatabase
        var profile: UserProfile? = null

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USERNAME, COLUMN_ROLE, COLUMN_ADDRESS, COLUMN_WALLET),
            "LOWER($COLUMN_USERNAME) = LOWER(?)",
            arrayOf(username.trim()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val userIdx = cursor.getColumnIndex(COLUMN_USERNAME)
            val roleIdx = cursor.getColumnIndex(COLUMN_ROLE)
            val addrIdx = cursor.getColumnIndex(COLUMN_ADDRESS)
            val walletIdx = cursor.getColumnIndex(COLUMN_WALLET)

            if (userIdx != -1 && roleIdx != -1) {
                val uname = cursor.getString(userIdx)
                val role = cursor.getString(roleIdx)
                val addr = if (addrIdx != -1 && !cursor.isNull(addrIdx)) cursor.getString(addrIdx) else "123 University Belt, Manila"
                val wallet = if (walletIdx != -1 && !cursor.isNull(walletIdx)) cursor.getDouble(walletIdx) else 1500.00

                profile = UserProfile(uname, role, addr, wallet)
            }
        }
        cursor.close()
        db.close()
        return profile
    }

    fun updateUserAddress(username: String, newAddress: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ADDRESS, newAddress)
        }
        val count = db.update(TABLE_USERS, values, "LOWER($COLUMN_USERNAME) = LOWER(?)", arrayOf(username.trim()))
        db.close()
        return count > 0
    }

    fun topUpWallet(username: String, amount: Double): Boolean {
        val profile = getUserProfile(username) ?: return false
        val newBalance = profile.walletBalance + amount

        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WALLET, newBalance)
        }
        val count = db.update(TABLE_USERS, values, "LOWER($COLUMN_USERNAME) = LOWER(?)", arrayOf(username.trim()))
        db.close()
        return count > 0
    }

    fun checkUsernameExists(username: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "LOWER($COLUMN_USERNAME) = LOWER(?)",
            arrayOf(username.trim()),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun validateLogin(username: String, password: String): String? {
        val db = this.writableDatabase
        var role: String? = null
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ROLE),
            "LOWER($COLUMN_USERNAME) = LOWER(?) AND $COLUMN_PASSWORD = ?",
            arrayOf(username.trim(), password.trim()),
            null, null, null
        )
        if (cursor.moveToFirst()) {
            val roleIndex = cursor.getColumnIndex(COLUMN_ROLE)
            if (roleIndex != -1) {
                role = cursor.getString(roleIndex)
            }
        }
        cursor.close()
        db.close()
        return role
    }

    fun addMenuItem(storeName: String, itemName: String, description: String, price: Double, category: String, imageType: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MENU_STORE, storeName)
            put(COLUMN_ITEM_NAME, itemName)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_PRICE, price)
            put(COLUMN_MENU_CATEGORY, category)
            put(COLUMN_ITEM_IMAGE, imageType)
            put(COLUMN_ITEM_RATING, 4.8)
            put(COLUMN_ITEM_PREP, 15)
        }
        val result = db.insert(TABLE_MENU, null, values)
        db.close()
        return result != -1L
    }

    fun deleteMenuItem(itemId: Int): Boolean {
        val db = this.writableDatabase
        val count = db.delete(TABLE_MENU, "$COLUMN_ITEM_ID = ?", arrayOf(itemId.toString()))
        db.close()
        return count > 0
    }

    fun searchAndSortMenuItems(
        query: String,
        categoryFilter: String,
        sortBy: String
    ): List<MenuItem> {
        val itemList = mutableListOf<MenuItem>()
        val db = this.readableDatabase

        var selection = "1=1"
        val args = mutableListOf<String>()

        if (query.isNotBlank()) {
            selection += " AND ($COLUMN_ITEM_NAME LIKE ? OR $COLUMN_DESCRIPTION LIKE ? OR $COLUMN_MENU_STORE LIKE ?)"
            val wildcard = "%${query.trim()}%"
            args.add(wildcard)
            args.add(wildcard)
            args.add(wildcard)
        }

        if (categoryFilter.isNotBlank() && !categoryFilter.equals("All", ignoreCase = true)) {
            selection += " AND $COLUMN_MENU_CATEGORY = ?"
            args.add(categoryFilter)
        }

        val orderBy = when (sortBy) {
            "Rating" -> "$COLUMN_ITEM_RATING DESC"
            "PriceLow" -> "$COLUMN_PRICE ASC"
            "PriceHigh" -> "$COLUMN_PRICE DESC"
            "Fastest" -> "$COLUMN_ITEM_PREP ASC"
            else -> "$COLUMN_ITEM_ID DESC"
        }

        val cursor = db.query(TABLE_MENU, null, selection, args.toTypedArray(), null, null, orderBy)

        if (cursor.moveToFirst()) {
            val idIdx = cursor.getColumnIndex(COLUMN_ITEM_ID)
            val storeIdx = cursor.getColumnIndex(COLUMN_MENU_STORE)
            val nameIdx = cursor.getColumnIndex(COLUMN_ITEM_NAME)
            val descIdx = cursor.getColumnIndex(COLUMN_DESCRIPTION)
            val priceIdx = cursor.getColumnIndex(COLUMN_PRICE)
            val catIdx = cursor.getColumnIndex(COLUMN_MENU_CATEGORY)
            val imgIdx = cursor.getColumnIndex(COLUMN_ITEM_IMAGE)
            val ratingIdx = cursor.getColumnIndex(COLUMN_ITEM_RATING)
            val prepIdx = cursor.getColumnIndex(COLUMN_ITEM_PREP)

            do {
                if (idIdx != -1 && storeIdx != -1 && nameIdx != -1 && descIdx != -1 && priceIdx != -1 && catIdx != -1 && imgIdx != -1) {
                    val ratingVal = if (ratingIdx != -1) cursor.getDouble(ratingIdx) else 4.8
                    val prepVal = if (prepIdx != -1) cursor.getInt(prepIdx) else 15

                    itemList.add(
                        MenuItem(
                            itemId = cursor.getInt(idIdx),
                            storeName = cursor.getString(storeIdx),
                            itemName = cursor.getString(nameIdx),
                            description = cursor.getString(descIdx),
                            price = cursor.getDouble(priceIdx),
                            category = cursor.getString(catIdx),
                            imageType = cursor.getString(imgIdx),
                            rating = ratingVal,
                            prepTimeMins = prepVal
                        )
                    )
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return itemList
    }

    fun placeOrder(
        customerName: String,
        storeName: String,
        itemName: String,
        paymentType: String,
        totalPrice: Double,
        specialNote: String
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CUSTOMER_NAME, customerName)
            put(COLUMN_ORDER_STORE, storeName)
            put(COLUMN_ORDER_ITEM, itemName)
            put(COLUMN_PAYMENT_TYPE, paymentType)
            put(COLUMN_TOTAL_PRICE, totalPrice)
            put(COLUMN_SPECIAL_NOTE, specialNote)
            put(COLUMN_ORDER_STATUS, "Preparing")
            put(COLUMN_ORDER_RATING, 0)
            put(COLUMN_ORDER_REVIEW, "")
        }
        val result = db.insert(TABLE_ORDERS, null, values)
        db.close()
        return result != -1L
    }

    fun updateOrderStatus(orderId: Int, newStatus: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ORDER_STATUS, newStatus)
        }
        val count = db.update(TABLE_ORDERS, values, "$COLUMN_ORDER_ID = ?", arrayOf(orderId.toString()))
        db.close()
        return count > 0
    }

    fun rateOrder(orderId: Int, ratingStars: Int, reviewText: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ORDER_RATING, ratingStars)
            put(COLUMN_ORDER_REVIEW, reviewText)
        }
        val count = db.update(TABLE_ORDERS, values, "$COLUMN_ORDER_ID = ?", arrayOf(orderId.toString()))
        db.close()
        return count > 0
    }

    fun getCustomerOrders(customerName: String): List<Order> {
        val orderList = mutableListOf<Order>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_ORDERS,
            null,
            "LOWER($COLUMN_CUSTOMER_NAME) = LOWER(?)",
            arrayOf(customerName.trim()),
            null, null,
            "$COLUMN_ORDER_ID DESC"
        )

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(COLUMN_ORDER_ID)
            val nameIndex = cursor.getColumnIndex(COLUMN_CUSTOMER_NAME)
            val storeIndex = cursor.getColumnIndex(COLUMN_ORDER_STORE)
            val itemIndex = cursor.getColumnIndex(COLUMN_ORDER_ITEM)
            val paymentIndex = cursor.getColumnIndex(COLUMN_PAYMENT_TYPE)
            val priceIndex = cursor.getColumnIndex(COLUMN_TOTAL_PRICE)
            val noteIndex = cursor.getColumnIndex(COLUMN_SPECIAL_NOTE)
            val statusIndex = cursor.getColumnIndex(COLUMN_ORDER_STATUS)
            val ratingIndex = cursor.getColumnIndex(COLUMN_ORDER_RATING)
            val reviewIndex = cursor.getColumnIndex(COLUMN_ORDER_REVIEW)

            do {
                if (idIndex != -1 && nameIndex != -1 && itemIndex != -1 && paymentIndex != -1 && priceIndex != -1) {
                    val storeNameStr = if (storeIndex != -1 && !cursor.isNull(storeIndex)) cursor.getString(storeIndex) else "BiteDash Express"
                    val noteStr = if (noteIndex != -1 && !cursor.isNull(noteIndex)) cursor.getString(noteIndex) else ""
                    val statusStr = if (statusIndex != -1 && !cursor.isNull(statusIndex)) cursor.getString(statusIndex) else "Preparing"
                    val ratingVal = if (ratingIndex != -1 && !cursor.isNull(ratingIndex)) cursor.getInt(ratingIndex) else 0
                    val reviewStr = if (reviewIndex != -1 && !cursor.isNull(reviewIndex)) cursor.getString(reviewIndex) else ""

                    val order = Order(
                        orderId = cursor.getInt(idIndex),
                        customerName = cursor.getString(nameIndex),
                        itemName = "$storeNameStr - ${cursor.getString(itemIndex)}",
                        paymentType = cursor.getString(paymentIndex),
                        totalPrice = cursor.getDouble(priceIndex),
                        specialNote = noteStr,
                        status = statusStr,
                        rating = ratingVal,
                        review = reviewStr
                    )
                    orderList.add(order)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return orderList
    }

    fun getAllOrders(): List<Order> {
        val orderList = mutableListOf<Order>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ORDERS ORDER BY $COLUMN_ORDER_ID DESC", null)

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(COLUMN_ORDER_ID)
            val nameIndex = cursor.getColumnIndex(COLUMN_CUSTOMER_NAME)
            val storeIndex = cursor.getColumnIndex(COLUMN_ORDER_STORE)
            val itemIndex = cursor.getColumnIndex(COLUMN_ORDER_ITEM)
            val paymentIndex = cursor.getColumnIndex(COLUMN_PAYMENT_TYPE)
            val priceIndex = cursor.getColumnIndex(COLUMN_TOTAL_PRICE)
            val noteIndex = cursor.getColumnIndex(COLUMN_SPECIAL_NOTE)
            val statusIndex = cursor.getColumnIndex(COLUMN_ORDER_STATUS)
            val ratingIndex = cursor.getColumnIndex(COLUMN_ORDER_RATING)
            val reviewIndex = cursor.getColumnIndex(COLUMN_ORDER_REVIEW)

            do {
                if (idIndex != -1 && nameIndex != -1 && itemIndex != -1 && paymentIndex != -1 && priceIndex != -1) {
                    val storeNameStr = if (storeIndex != -1 && !cursor.isNull(storeIndex)) cursor.getString(storeIndex) else "BiteDash Express"
                    val noteStr = if (noteIndex != -1 && !cursor.isNull(noteIndex)) cursor.getString(noteIndex) else ""
                    val statusStr = if (statusIndex != -1 && !cursor.isNull(statusIndex)) cursor.getString(statusIndex) else "Preparing"
                    val ratingVal = if (ratingIndex != -1 && !cursor.isNull(ratingIndex)) cursor.getInt(ratingIndex) else 0
                    val reviewStr = if (reviewIndex != -1 && !cursor.isNull(reviewIndex)) cursor.getString(reviewIndex) else ""

                    val order = Order(
                        orderId = cursor.getInt(idIndex),
                        customerName = cursor.getString(nameIndex),
                        itemName = "$storeNameStr - ${cursor.getString(itemIndex)}",
                        paymentType = cursor.getString(paymentIndex),
                        totalPrice = cursor.getDouble(priceIndex),
                        specialNote = noteStr,
                        status = statusStr,
                        rating = ratingVal,
                        review = reviewStr
                    )
                    orderList.add(order)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return orderList
    }
}