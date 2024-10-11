# readFileFromArtifactStorage() step

This step tries to read the file from the given URL. The URL should point to the Testing Farm artifact storage.

## Parameters

* **url**: string; File URL
* **timeoutSeconds**: int (optional); Timeout in seconds (default: 900)
* **suppressSslErrors**: boolean; (optional) ignore ssl errors (default: false)

## Example Usage

```groovy
def fileContent = readFileFromArtifactStorage(url: 'https://example.com/file.xml', timeoutSeconds: 60)
```
