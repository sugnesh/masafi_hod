package com.engear.vansales.interfaces

import com.engear.vansales.model.ItemData

interface OnDeliveryItemClickListener {
    fun onItemsDeliveryClick(item: ItemData.Data, position: Int, tag: String)
}