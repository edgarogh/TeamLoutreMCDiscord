package fr.edgarogh.tlmcd.util

import java.util.*

enum class LetterType(alphabet: String) {
    VOWEL("aeiouy"),
    CONSONANT("bcdfghjklmnpqrstvwxz");

    private val alphabet: CharArray = alphabet.toCharArray()

    fun randomChar() = alphabet.random()

    fun opposite() = if (this == VOWEL) CONSONANT else VOWEL

    companion object {

        fun of(boolean: Boolean): LetterType {
            return if (boolean) VOWEL else CONSONANT
        }

        fun random(random: Random = Random()): LetterType {
            return of(random.nextBoolean())
        }

    }
}

/**
 * @param length Length of the requested string
 * @param type LetterType.VOWEL or LetterType.CONSONANT
 */
tailrec fun getRandomPronounceableString(
    length: Int,
    type: LetterType = LetterType.random(),
    acc: String = ""
): String =
    if (length == 0) {
        acc
    }
    else {
        val char = type.randomChar()
        getRandomPronounceableString(length - 1, type.opposite(), acc + char)
    }
