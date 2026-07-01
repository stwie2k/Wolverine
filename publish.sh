
start_time=$(date +%s)

./gradlew clean assemble -DisMTL=false --profile -x :app:assemble

gradlew_exit_status=$?
if [ $gradlew_exit_status -ne 0 ]; then
    echo "Gradle command failed with exit status $gradlew_exit_status"
    exit $gradlew_exit_status
fi

sleep 5s
./gradlew publish -DisMTL=false --profile

end_time=$(date +%s)

cost_time=$((end_time - start_time))
echo "publish cost time: $cost_time seconds"