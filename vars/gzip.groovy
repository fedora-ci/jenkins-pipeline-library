#!/usr/bin/groovy

import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * gzip() step.
 */
def call(String s) {

    if (s == null || s.isEmpty()) {
        return s
    }

	def targetStream = new ByteArrayOutputStream()
	def zipStream = new GZIPOutputStream(targetStream)
	zipStream.write(s.getBytes('UTF-8'))
	zipStream.close()
	def zippedBytes = targetStream.toByteArray()
	targetStream.close()
	return zippedBytes.encodeBase64().toString()
}
