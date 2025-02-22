package ink.ptms.chemdah.core.quest.selector

import com.google.common.collect.ImmutableMap
import ink.ptms.chemdah.core.quest.selector.Flags.Companion.matchType
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.invokeMethod
import taboolib.module.nms.MinecraftVersion

/**
 * Chemdah
 * ink.ptms.chemdah.util.Mats
 *
 * stone[power=5] —— Matches all blocks of type STONE and which have a power of 5
 * ^stone —— Matches all blocks of type name starts with "stone"
 * stone$ —— Matches all blocks of type name ends with "stone"
 * (stone) —— Matches all blocks of type name contains "stone"
 * %slabs% —— Matches all slabs
 * *[power=5] —— Matches everything and which have a power of 5
 *
 *
 * @author sky
 * @since 2021/3/2 5:41 下午
 */
class InferBlock(val mats: List<Block>) {

    fun isBlock(block: org.bukkit.block.Block): Boolean {
        val type = block.type.name.toLowerCase()
        val data = when {
            MinecraftVersion.majorLegacy >= 11800 -> {
                block.blockData.getProperty<Any>("state")!!.invokeMethod<Map<Any, Any>>("getValues")!!.mapKeys { it.key.invokeMethod<String>("getName")!! }
            }
            MinecraftVersion.majorLegacy >= 11300 -> {
                block.blockData.getProperty<Any>("state")!!.invokeMethod<Map<Any, Any>>("getStateMap")!!.mapKeys { it.key.invokeMethod<String>("getName")!! }
            }
            else -> emptyMap()
        }
        return mats.any { mat -> mat.matchFlags(type) && mat.matchBlockData(data) }
    }

    data class Block(val material: String, val flags: List<Flags>, val data: Map<String, String>) {

        fun matchFlags(type: String): Boolean {
            return flags.any { it.match(type, material) }
        }

        fun matchBlockData(map: Map<String, Any>): Boolean {
            return data.all { map[it.key]?.toString() == it.value }
        }
    }

    companion object {

        fun List<String>.toInferBlock() = InferBlock(map { it.toBlock() })

        private fun String.toBlock(): Block {
            val type: String
            val data = HashMap<String, String>()
            val flag = ArrayList<Flags>()
            if (indexOf('[') > -1 && endsWith(']')) {
                type = substring(0, indexOf('['))
                // 只有 1.13+ 才允许加载 BlockData 选择器
                if (MinecraftVersion.majorLegacy >= 11300) {
                    data.putAll(substring(indexOf('[') + 1, length - 1).split(",").map {
                        it.trim().split("=").run { get(0) to (getOrNull(1) ?: get(0)) }
                    })
                }
            } else {
                type = this
            }
            return Block(type.matchType(flag), flag, data)
        }
    }
}