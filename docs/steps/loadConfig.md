# loadConfig() step

This step loads the pipeline configuration from a config file.
Optionally, if the profile name is specified, only the profile configuration is returned.
The config file needs to be in the same repository with the pipeline definition (Jenkinsfile).
The default name of the config file is `config.json`. 


## Parameters

* **profile**: string; (optional) name of the test profile to load

## Example Usage

```groovy
loadConfig(profile: 'f35')
```
