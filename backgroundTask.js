import {NativeModules} from 'react-native';

module.exports = async () => {
  console.log("Hey this is a headless JS task==========================");
  NativeModules.SharedStorage.set(JSON.stringify({text:Math.random().toString()}));
};