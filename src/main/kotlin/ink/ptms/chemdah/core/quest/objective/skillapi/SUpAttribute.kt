package ink.ptms.chemdah.core.quest.objective.skillapi

import com.sucy.skill.api.event.PlayerUpAttributeEvent
import ink.ptms.chemdah.core.quest.objective.Dependency
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI

/**
 * Chemdah
 * ink.ptms.chemdah.core.quest.objiective.skillapi.SUpAttribute
 *
 * @author Peng_Lx
 * @since 2021/5/29 7:59 下午
 */
@Dependency("SkillAPI")

object SUpAttribute : ObjectiveCountableI<PlayerUpAttributeEvent>() {

    override val name = "skillapi up attribute"
    override val event = PlayerUpAttributeEvent::class.java

    init {
        handler {
            it.playerData.player
        }
        addSimpleCondition("position") { data, it ->
            data.toPosition().inside(it.playerData.player.location)
        }
        addSimpleCondition("attribute") { data, it ->
            toString().equals(it.attribute, true)
        }
        addConditionVariable("attribute") {
            it.attribute
        }
    }
}