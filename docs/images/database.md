## Tables
```text
attachments
audit_logs
flyway_schema_history
password_reset_tokens
refresh_tokens
roles
user_roles
users
workflow_comment
workflow_histories
workflow_requests
```

## Relationship
```
attachments -> workflow_requests
audit_logs -> users
password_reset_tokens -> users
refresh_tokens -> users
user_roles -> roles
user_roles -> users
workflow_comment -> users
workflow_comment -> workflow_requests
workflow_histories -> users
workflow_histories -> workflow_requests
workflow_requests -> users
``` 

