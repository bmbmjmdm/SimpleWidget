import {NativeModules} from 'react-native';

const SharedStorage = NativeModules.SharedStorage;

module.exports = async (taskData) => {
  console.log("Hey this is a headless JS task==========================");
  SharedStorage.get().then((value) => {
    let newVal = value
    if (!newVal) newVal = "";
    SharedStorage.set(JSON.stringify("1" + newVal));
  });
  return;
};