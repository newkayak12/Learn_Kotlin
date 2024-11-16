package ch05


fun forEach(a: IntArray, action: (Int) -> Unit) {
    for(n in a) action(n)
}
fun main() {
    forEach(intArrayOf(1, 2, 3, 4)) {
        if( it < 2 || it > 3 ) return@forEach
    }

    forEach(intArrayOf(1, 2, 3, 4), {it: Int ->
        if( it < 2 || it > 3) return@forEach
    } )
}