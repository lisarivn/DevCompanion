
package com.example.devcompanion.feature.devtools.regex

object RegexChecker {

    data class Match(
        val value: String,
        val start: Int,
        val endExclusive: Int,
        val groups: List<String?>
    )

    data class CheckResult(
        val matches: List<Match>
    ) {
        val matchCount: Int
            get() = matches.size
    }

    fun check(
        pattern: String,
        input: String
    ): Result<CheckResult> {
        return runCatching {
            val regex = Regex(pattern)

            val matches = regex.findAll(input).map { match ->
                Match(
                    value = match.value,
                    start = match.range.first,
                    endExclusive = match.range.last + 1,
                    groups = (1 until match.groups.size).map { index ->
                        match.groups[index]?.value
                    }
                )
            }.toList()

            CheckResult(matches)
        }
    }
}
