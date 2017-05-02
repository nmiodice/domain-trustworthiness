BUCKET=domain-trustworthiness
ZIP_NAME=$BUCKET.zip

PROJECT_S3_LOCATION=s3://$BUCKET/

mvn clean

# s3 upload
zip -9 -r --exclude=*.git* $ZIP_NAME *
aws s3 cp $ZIP_NAME $PROJECT_S3_LOCATION
rm $ZIP_NAME


# codebuild
#aws codebuild start-build --project-name DomainTrustworthinessBuild
