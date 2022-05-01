package org.spreadme.pdfgadgets.ui.toolbar

import org.spreadme.pdfgadgets.resources.R

enum class ViewMode(
    val icon: String,
    val desc: String
) {

    INFO(R.Icons.info, "信息"),
    OUTLINES(R.Icons.outlines, "概要"),
    STRUCTURE(R.Icons.structure, "结构"),
    SIGNATURE(R.Icons.signature, "签名");
}