package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itextpdf.kernel.pdf.PdfStream
import org.spreadme.pdfgadgets.model.StructureNode
import org.spreadme.pdfgadgets.resources.R
import org.spreadme.pdfgadgets.ui.common.VerticalScrollable
import org.spreadme.pdfgadgets.ui.common.clickable
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanelUIState
import org.spreadme.pdfgadgets.utils.choose

@Composable
fun StructureTree(
    structureRoot: StructureNode,
    sidePanelUIState: SidePanelUIState,
    onPdfStream: (PdfStream) -> Unit
) {
    VerticalScrollable(sidePanelUIState) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            structureRoot.childs().forEach { StructureNodeView(it, onPdfStream) }
        }
    }
}

@Composable
private fun StructureNodeView(
    node: StructureNode,
    onPdfStream: (PdfStream) -> Unit
) {
    var expanded by remember { node.expanded }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .selectable(true) { expanded = !expanded }
            .padding(start = (24 * node.level).dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        StructureNodePrefix(node.hasChild(), node.expanded)
        StructureNodeName(node, onPdfStream)
    }

    AnimatedVisibility(
        expanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Column {
            node.childs().forEach {
                StructureNodeView(it, onPdfStream)
            }
        }
    }

}

@Composable
private fun StructureNodePrefix(
    hasChild: Boolean,
    expanded: MutableState<Boolean>
) {
    if (hasChild) {
        Icon(
            expanded.value.choose(Icons.Default.ArrowDropDown, Icons.Default.ArrowRight),
            contentDescription = "",
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier.size(16.dp).clickable {
                expanded.value = !expanded.value
            }
        )
    } else {
        Box(modifier = Modifier.padding(start = 16.dp))
    }
}

@Composable
private fun StructureNodeName(
    node: StructureNode,
    onPdfStream: (PdfStream) -> Unit
) {
    Icon(
        painter = painterResource(node.type.icon),
        contentDescription = "",
        tint = MaterialTheme.colors.onBackground,
        modifier = Modifier.padding(horizontal = 8.dp).size(16.dp)
    )
    Text(
        text = node.toString(),
        style = MaterialTheme.typography.caption,
        color = if (node.isStream()) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.onBackground
        },
        textAlign = TextAlign.Start,
        textDecoration = if (node.isStream()) {
            TextDecoration.Underline
        } else {
            null
        },
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.run {
            if (node.isStream()) {
                this.clickable(true) {
                    onPdfStream(node.pdfObject as PdfStream)
                }
            } else {
                this
            }
        }
    )

    node.pdfObject.indirectReference?.let {
        Icon(
            painter = painterResource(R.Icons.pdfindirect_reference),
            contentDescription = "",
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(horizontal = 8.dp).size(16.dp)
        )
        Text(
            it.toString(),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onBackground,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}