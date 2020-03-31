package xyz.gnarbot.gnar.commands.`fun`

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.WordUtils
import xyz.gnarbot.gnar.commands.*

@Command(
        aliases = ["dialog"],
        usage = "(words...)",
        description = "Make some of that Windows ASCII art!"
)
@BotInfo(
        id = 40,
        category = Category.FUN
)
class DialogCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val lines = WordUtils
                .wrap(StringUtils.join(args, ' ').replace("```", ""), 25, null, true)
                .split("\n")

        context.send().embed {
            desc {
                buildString {
                    appendln("```")
                    appendln("╔═══════════════════════════╗ ")
                    appendln("║ Alert                     ║")
                    appendln("╠═══════════════════════════╣")

                    lines.map(String::trim)
                            .map {
                                it + buildString {
                                    repeat(25 - it.length) { append(' ') }
                                }
                            }
                            .map { "║ $it ║" }
                            .forEach { appendln(it) }

                    when ((Math.random() * 4).toInt()) {
                        0 -> {
                            appendln("║  ┌─────────┐  ┌────────┐  ║")
                            appendln("║  │   Yes   │  │   No   │  ║")
                            appendln("║  └─────────┘  └────────┘  ║")
                        }
                        1 -> {
                            appendln("║ ┌─────┐  ┌──────┐  ┌────┐ ║")
                            appendln("║ │ Yes │  │ Help │  │ No │ ║")
                            appendln("║ └─────┘  └──────┘  └────┘ ║")
                        }
                        2 -> {
                            appendln("║  ┌─────────────────┬───┐  ║")
                            appendln("║  │     Confirm     │ X │  ║")
                            appendln("║  └─────────────────┴───┘  ║")
                        }
                        3 -> {
                            appendln("║  ┌───────────┐ ┌───────┐  ║")
                            appendln("║  │ HELLA YES │ │ PUSSY │  ║")
                            appendln("║  └───────────┘ └───────┘  ║")
                        }
                    }

                    appendln("╚═══════════════════════════╝")
                    appendln("```")
                }
            }
        }.action().queue()
    }
}