# loadConfig() step

This step loads test profile configuration from the pipeline config file.
The config file needs to be in the same repository with the pipeline definition (Jenkinsfile).
The default name of the config file is `config.json`. 


## Parameters

* **profile**: string; name of the test profile to load

## Example Usage

```groovy
loadConfig(profile: 'f34')
```
