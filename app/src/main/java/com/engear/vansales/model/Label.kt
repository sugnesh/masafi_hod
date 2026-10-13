package com.engear.vansales.model

class Label {

    fun getLabel(labelId: String): String {
        try {
            /* val label =
                 Select().from(Label::class.java).where("label_Id=?", labelId).executeSingle<Label>()
             return label?.getLabelName() ?: labelId*/
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return labelId
    }
}