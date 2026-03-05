# Package Structure
Updated: 05-03-2026 12:17:45

```
safecube-android/
├── .build/com/safecube/tooling/
│   ├── FolderTreeToFile.class
│   └── Logger.class
├── .git/
│   ├── hooks/
│   │   ├── applypatch-msg.sample
│   │   ├── commit-msg.sample
│   │   ├── fsmonitor-watchman.sample
│   │   ├── post-update.sample
│   │   ├── pre-applypatch.sample
│   │   ├── pre-commit
│   │   ├── pre-commit.sample
│   │   ├── pre-merge-commit.sample
│   │   ├── pre-push.sample
│   │   ├── pre-rebase.sample
│   │   ├── pre-receive.sample
│   │   ├── prepare-commit-msg.sample
│   │   ├── push-to-checkout.sample
│   │   ├── sendemail-validate.sample
│   │   └── update.sample
│   ├── info/
│   │   └── exclude
│   ├── logs/
│   │   ├── refs/
│   │   │   ├── heads/
│   │   │   │   └── main
│   │   │   └── remotes/origin/
│   │   │       ├── HEAD
│   │   │       └── main
│   │   └── HEAD
│   ├── objects/
│   │   ├── 00/
│   │   │   ├── 3a775472820229a294cc772c48846bd7efdd58
│   │   │   └── 85d2b06cad004f9dc9188363711eabfad9a93c
│   │   ├── 01/
│   │   │   ├── 0bff67b9d1b7576159bd13f4aafe5934d6b3e4
│   │   │   ├── 5eee1bdb1d4e49211ce871426608670d114f9d
│   │   │   ├── 7499bbb498ae2e9c200e899fe8db172461afa0
│   │   │   ├── 83e16539ea5c64aa57bb80e45b4c0522a2d77e
│   │   │   ├── dd60e30db9371389f85358a419dd06e2804110
│   │   │   └── eebd0c990b21eb0bf8fb25e01d135fec760b63
│   │   ├── 02/
│   │   │   ├── 425cf9497b5aeb5834a1ba8d2cabc067e7ae39
│   │   │   └── b79f4e2569bd91dd3042aee2b43a7fed8ba861
│   │   ├── 03/
│   │   │   ├── 02a907bab9bb959ea3de5abd0521c8d0d16748
│   │   │   ├── 2cb27cf11130bed103b588de375a8568616875
│   │   │   ├── 4a01ba4eea11b9455534094bf69f0a6180c890
│   │   │   └── db60c84b78941a89204eebc02fca8a032cfdfc
│   │   ├── 04/
│   │   │   ├── 1139dfdea857d5d8605ab28487325746335b40
│   │   │   ├── 405d8688d57d7f69bb3111c5c6f72460beebed
│   │   │   └── f57d4b7e7280da918524f0667c4f465569b6cf
│   │   ├── 05/
│   │   │   ├── 269c9c243fd996ac7b01544eab998ef6970dd5
│   │   │   └── bfe10a3531cfafc88852f623f9e9f21ebfa15c
│   │   ├── 06/
│   │   │   ├── 0ff0a8906dc6a060c343a2a750f45f109a10dd
│   │   │   └── d9e2cc0094d4e415c097a0659b371bc962e658
│   │   ├── 07/
│   │   │   ├── 1ffd62e11295ccb6da1ab993ab0a13e4074d00
│   │   │   ├── 615786dd22a6c2df1ebd0b3a53fcd204d49d6c
│   │   │   ├── d5da9cbf141911847041df5d7b87f0dd5ef9d4
│   │   │   ├── e33d4eb4804bc328dfafd231fe2ad5e0e577e9
│   │   │   ├── eaa914c9c36eb75f4982a12b985e5e0de93c1d
│   │   │   └── f8a293d358c592da2fd5d94c0a87f6e7a84d60
│   │   ├── 08/
│   │   │   └── 39040535d42f11e32ee55a89b501208c1003ed
│   │   ├── 09/
│   │   │   ├── 340652b33fc9b4634449463062d73cfe5fe63c
│   │   │   ├── 7df47b4890ca282405dfc62c38cc6f892ee15b
│   │   │   ├── c069386c6e4f53f02c04b0ac0eed69b7ae6ce5
│   │   │   └── dd842aec2b5a57784c43973573f2c0d172e21e
│   │   ├── 0a/
│   │   │   ├── 1dd475a2da796e3c56468e5c3632ab4f7c794f
│   │   │   ├── 3b95d7519b589a1e80157bb91ef43fd3482f12
│   │   │   ├── 5a4ec9eb7ef4769c1071255f7b0c413456a2d6
│   │   │   └── cc9c14291a7fe692b2e807e64692cfeb9ce00c
│   │   ├── 0b/
│   │   │   ├── 4ad92696fb520d38b4a7c64eb6e3344743faf1
│   │   │   ├── 4e84bd6003347d2e9c8c61e96da643a4613431
│   │   │   ├── c36827f9295cba0a666ef7dac53da0bae43550
│   │   │   ├── c8cc8bdfc7d89a0827c420547f055e81e3f8f0
│   │   │   └── cbe77329145a834617d2722de22912ca8f4994
│   │   ├── 0c/
│   │   │   ├── 41d557b0da5f5738371b3b8e50d8dd322f0344
│   │   │   ├── 529a80fba79f00d435b23a10b71e5295fecce0
│   │   │   ├── 61322cc43f73fa99cf855d07059d17891f643b
│   │   │   ├── d77861eeb6e110b843697bea47c856be291b8d
│   │   │   └── fd904e0d8b966bb3d023e859d6019ea0e326a7
│   │   ├── 0d/
│   │   │   ├── 0ae1971e5679e912812f3842ff69483f372448
│   │   │   ├── 4993fa306bcaadd2857c0c8cf279637bccef0d
│   │   │   ├── b333f77eb71e86e44e54ba48f265133a92b8f9
│   │   │   ├── c800f9cfa54ef5e773b35b73b0a39b64373348
│   │   │   ├── e7c7e781cf32d23fd144c5f7e9edcf468b4394
│   │   │   └── f1549941e0b5fb3130b220a50f03ebb7f0b8a4
│   │   ├── 0e/
│   │   │   ├── 44603db2224baf0d99ea5eb5078c2e1ab44d62
│   │   │   ├── 77476c9acb921e0e876fc4360fd304a1c760d7
│   │   │   └── ede3b7289fda8484c55509b60ac7c81704cd7a
│   │   ├── 0f/
│   │   │   ├── 32359363d94259d52986f15de749b87adae20a
│   │   │   ├── 32c5a3e635ddb9bafab08a77cbced20cbd075a
│   │   │   ├── 9ff994210957210533f6f4110fd9399575cb7c
│   │   │   ├── d77326f4086a7c80a1b8951c6616853c706928
│   │   │   └── eb1401bf57b428a7c75c608ee11fa29a492450
│   │   ├── 10/
│   │   │   ├── 468b2e1116249c036be9eb1309ac3ea2ce9b9a
│   │   │   ├── 68f05cbcc4285a690247054af12c619406daf7
│   │   │   ├── 97fb19bc6ad8f28a3e3fcd43fd87133d45fea1
│   │   │   └── c46deb58dfac668602a24ac83b5062d14bc2ae
│   │   ├── 11/
│   │   │   ├── 5cb396107ce1970b6ffd33026a1b336bf81893
│   │   │   ├── 69ab6e3ed07e045fff782860adc588570f9919
│   │   │   ├── 7581cbeaa34176c330b1bdfbaad8fd7dd5fa16
│   │   │   ├── 7b6ed75bdabe192df275613fbb9e5a143f9321
│   │   │   └── 89f7a620e266dfe73e0822e3ef90e3d58b4627
│   │   ├── 12/
│   │   │   ├── 17e928894cecf247fd980964e5e9f7fad16e4e
│   │   │   └── e06860941a1e76a4ade377689514bed72c8756
│   │   ├── 13/
│   │   │   ├── 044597ae73290e479a56c977bdfe1935a30707
│   │   │   ├── 705f70ae4972f00d872981534d1499f60b02c7
│   │   │   ├── c5a50bdc7387359698acce579f7e0b18b1abeb
│   │   │   └── efc8d1a60da3479e1b35d16404c8b5e8891569
│   │   ├── 14/
│   │   │   └── e74024fc96d9151b9cf7d08a01c99127e4b2c8
│   │   ├── 15/
│   │   │   ├── 5867a98a04f0c74e0fd73d09a7848a581cb677
│   │   │   ├── 66c81b85b9842f3df48af22d3783c9c015cab4
│   │   │   ├── 71cf6d857e0737915346e662e998878bbbc9a2
│   │   │   ├── d2cd681f451e9b705678ea48e7a95b008991cf
│   │   │   └── de52fd9254fd13a8dccca1970967ae22432115
│   │   ├── 16/
│   │   │   ├── 3c72738c7d4ba9d0650101be4f8f5b015e57fd
│   │   │   ├── 7ba919e8a36e0c03b4a333201192a1ddf8dac9
│   │   │   ├── 8004e46ac7ded0fa8c5d23397b3fad29db506a
│   │   │   ├── 81343baa4691335b5bd30b97fd91353a2b6aca
│   │   │   ├── a75efc8c4bfe00ad7c7978a1af00ef2dfa7eaf
│   │   │   └── b14f5da9e2fcd6f3f38cc9e584cef2f3c90ebe
│   │   ├── 17/
│   │   │   ├── 199ecc4301be97e2eed960c5c629d97c88ba9a
│   │   │   ├── 2633fac36c0e8cfc90019f5018ad4ed3d6ef93
│   │   │   └── 7d7317bc4fcc60c1d90db4dfdbc9bc3ef17aa5
│   │   ├── 18/
│   │   │   ├── 318bec5606eb601a449bbf68dab1765c773fd5
│   │   │   ├── 5f4bd76ffeb06f65c61abf512f2458187c0739
│   │   │   └── ffeca3207621abf16d8a6b014a785d181a45e0
│   │   ├── 19/
│   │   │   ├── 5c58325fa2ca527edb20972d025ef9e6cbfc9d
│   │   │   ├── 74b7b8224c700086a4d48e7e75bb8590eb71cb
│   │   │   ├── 80fbf71cd9eeb1d3e44057f2adac9e1fabebb2
│   │   │   └── b425d29eb7b3cf03f412de05ab1e7487a0f4b2
│   │   ├── 1a/
│   │   │   ├── 398a406cd0a53870d007b63b82bb5d592936f3
│   │   │   ├── 3bcb5eb9fd7633cb6845b445cac2a09fe8bc93
│   │   │   ├── 3ffbf46f8cf6633918c1840e9724b7a62195c0
│   │   │   ├── 8f40a13118e878e29e0bd8534ca18809dc1755
│   │   │   ├── 98721f5050ac25e33dce918c21ebf469198aa4
│   │   │   └── d7d2c4fac9669b3ab0c4f9a00ef3e6c050df98
│   │   ├── 1b/
│   │   │   ├── 732802c37d944333cd2565279da596abb48aa4
│   │   │   ├── 91b9ed5c16399091d90a9880eb85e4f48f6459
│   │   │   ├── 9a6956b3acdc11f40ce2bb3f6efbd845cc243f
│   │   │   └── cbb8deb3567e14d5d49a36a8a16349e11f98f8
│   │   ├── 1c/
│   │   │   ├── 8c696baa6a26a29aa4e280fdfca6a5cdf6cf0a
│   │   │   ├── 947456c91e169c6c898edf6ccddaaedbd32662
│   │   │   ├── a46215b5733c94943828dd16a6f54014ebad27
│   │   │   └── c349d91fdf64caf342b24b345689d46bda86fb
│   │   ├── 1d/
│   │   │   ├── 32a89eb4e719c1d7e60501bea7b601f9e47238
│   │   │   ├── 520a78f6872d9f33b084ca2074a198756abe74
│   │   │   ├── 5616bc59bba6736ec049ae67a396169bd160c9
│   │   │   ├── b938475c9325f85f081355eedd4b4381e78475
│   │   │   └── e484be8aadec4e17470dbd152152097d82404f
│   │   ├── 1e/
│   │   │   ├── 84302815bd4a4a94e6b48e9ac5ad1030f6c05d
│   │   │   └── 8d672d17aae5cc55df896f94e2c6bd176a62c8
│   │   ├── 1f/
│   │   │   └── 5c331dfc9db4f0717ddfbaeb803b7feff2cd55
│   │   ├── 20/
│   │   │   ├── 59a97761e342957d427aa1d3e4ac0978c0e5a3
│   │   │   ├── 623638bfe1bb36d9da83f4f5a370c33b4c678a
│   │   │   └── be30a4c93a9024372230f00ef1d7333d2db025
│   │   ├── 21/
│   │   │   ├── 47475c59302e1c127ef510c5ada1b9c41e8293
│   │   │   ├── 806fc9a4d5f2a227859d9be151d1edcfa093b4
│   │   │   └── 96139d35ea871f7c512dbe23a8e27b490ddfa1
│   │   ├── 22/
│   │   │   ├── 57eefb499b2e6853d44660d2738c09191028cf
│   │   │   ├── 614134fa20d1def99d614e47f536d3fb457274
│   │   │   └── 914160c6c54bf9267f3e72130a0ac75be28248
│   │   ├── 23/
│   │   │   ├── 04f26746a3445afa4d3a5d34c8d13f97eab7e6
│   │   │   ├── 19b665af5686f886987d5605c4dbc4047d5791
│   │   │   ├── 410c0bfadd06927110a34bbba0935289904b16
│   │   │   ├── 6d8237df1bf99cf9efe977b3cf2046eb2612c4
│   │   │   ├── bcdacca88e9fcb3f8f91c0d84ed89c75907504
│   │   │   └── c21576c1e4cf51dac9f57721f80e06707c6c22
│   │   ├── 24/
│   │   │   ├── 8dc7d7b51da68fd5d5e12bb1e0bafc267072d4
│   │   │   └── 9401db2bd18efda41a7e1bde87c753775c3a2e
│   │   ├── 25/
│   │   │   ├── 02b431297b83443816f3b61097061fe7ddfbd2
│   │   │   ├── 12bd1e07b6577c3f13923e16a8f73411ef3841
│   │   │   ├── 59d026b2d2f5be9b9f5e13898c7b6664a8680a
│   │   │   └── 7840f0c7c0d1a8defd4b287444aa85d02d380c
│   │   ├── 26/
│   │   │   ├── 1cc5925eeb23ebfe5eb8db0dc76513f0b552c4
│   │   │   ├── 6f031937b724be4c5995cfcd4480b7cdee1703
│   │   │   └── 9dde2be53f4eb6cd1277dbe25344dc3c7dd9ff
│   │   ├── 27/
│   │   │   ├── 759b48ce49142bae6783895103fc6dc73aaeaf
│   │   │   ├── 8ba063cf44d09f83451748f5cfe6d2e31cb09a
│   │   │   └── d4b6dd3d2e8f1722781d526be1e9a9ec683f10
│   │   ├── 28/
│   │   │   ├── 4efff0aa80a5f9f0d817ee7af2b63791c19388
│   │   │   ├── d4b77f9f036a47549d47db79c16788749dca10
│   │   │   └── d99e79882791a17150e9379e82886ab4387c4e
│   │   ├── 29/
│   │   │   ├── a1a10edea8f420b782b72ae1e901ee35bf3e1b
│   │   │   ├── aa2a0b8031820ebc23a8eb1cfc60fd33ee0d6e
│   │   │   └── fb98e342f6d0e7b166fcebd141c727e1a82b95
│   │   ├── 2a/
│   │   │   ├── 8192ccac5e459a5355d97ed6a719cd0da7c1a0
│   │   │   ├── 8a29e9c349313d1848fb1e26b0520fa181b439
│   │   │   ├── 8a6dc01848b19411780d418e888819060ee518
│   │   │   ├── a6214549230550bb375e80d5093173e85a3aa1
│   │   │   ├── ca75e3699df45bdac008297336c3c94d21f31a
│   │   │   └── d227125b4f3237115b01ab753020ef97ce78e9
│   │   ├── 2b/
│   │   │   ├── 068d11462a4b96669193de13a711a3a36220a0
│   │   │   ├── 393eb5d46a49955564f35ba0915c81f502be73
│   │   │   ├── 8a3770648d64ea06f49f0b5780febd5acb10b0
│   │   │   └── 917bc3046670cb713b1f7841d769aa9f23a1d1
│   │   ├── 2c/
│   │   │   ├── 034bddff580de4088f042e841e1b32fbda2bda
│   │   │   ├── 06fec36ab9af05bcbe066a5a57ff43ca80c469
│   │   │   ├── 41e40a600297c268ca40818e21e4e8f9d6d805
│   │   │   └── 73501a2db84e29de6e16fbb0a0c052524be550
│   │   ├── 2d/
│   │   │   ├── 46e142548e6201d127d6a57c5f687c1bdcd4a6
│   │   │   ├── 9585ee2216cf1f0ba0b463d0946658665090ea
│   │   │   ├── aa75f158751560e13ef3ac2f2192dda3556eb3
│   │   │   └── b41d4278368ceb3c729518456bc842a746f713
│   │   ├── 2e/
│   │   │   └── 733ce8688e4fb11d5e658c89b8cfc12d14203d
│   │   ├── 2f/
│   │   │   ├── 05551d29180d44e88f3848f4acdcc5054fc197
│   │   │   └── 6f5ab102fbf4e4e2f50a3300c73119ac281515
│   │   ├── 30/
│   │   │   ├── 66bb1efda44daceae1e9e4307b5d78a46d009f
│   │   │   ├── 6de0a7c896e091b0f401d3b5eff7129ab83c1f
│   │   │   ├── 9ff6fb344fb654144a10eb59b23a87ffdea37e
│   │   │   └── caf89991ec161a9c22985438312a3855805f19
│   │   ├── 31/
│   │   │   ├── 3bc44067c5725e0b0beaa663279e0fc576383e
│   │   │   ├── 435ef8d24d5bf544b9d326cf5952566c5cb013
│   │   │   └── e35dba2a2ebf57fcfcc413dff996cd20c5198f
│   │   ├── 32/
│   │   │   ├── 5df8d6e2a765a038df3c9e069f574c8ecbf851
│   │   │   ├── 731de198adba3f03f606d3691f3aa3f53bfc67
│   │   │   └── f9e261a456cfc87c359f4489a04566851cea78
│   │   ├── 33/
│   │   │   ├── 6ebd4f9a8eb11392097d25879c3b0db7d35af4
│   │   │   ├── 938c01dc74998583f3f9606174bf403f6b0665
│   │   │   └── dab1663b36aaf58aa3278d2b0c27c90618b3c5
│   │   ├── 34/
│   │   │   ├── 2c3954bd1f74df042516ec21f0f3b5490d6050
│   │   │   └── fe98c03e8ac9aaa85ff997cb5fe480f144f7da
│   │   ├── 35/
│   │   │   ├── 6feaefc4390fce28dfdfbb2171d35f7f56603f
│   │   │   └── 8b4c270bf7f972df9cace13cf7eb29a29cabe0
│   │   ├── 36/
│   │   │   ├── 6df0e371f8bb635c3e003d7a0b1fa82471adfe
│   │   │   └── de6ba0b7ae6617c6b4e8209f2a78ad408af451
│   │   ├── 37/
│   │   │   ├── 11582dcd1bd83f490c55fe19dedae2fa8d1474
│   │   │   ├── 5810dec85a0106a69e78cacaad8660159e59b1
│   │   │   ├── b88744023995d434c8ea12d2436f65947ab976
│   │   │   ├── d0b585308aeecb1ebdc191610e6d1c228b4d95
│   │   │   └── f362d0f8cb1bc7258fdbe43568f6c1a6b3096f
│   │   ├── 38/
│   │   │   ├── 736dd4ae730503d66651d7b1cb62bea5bb0ec1
│   │   │   ├── caacfff9055494549e665afbcaabf3e1bd3668
│   │   │   ├── dd217ae53a1873f096b6a1267687e1892c1b50
│   │   │   └── eb05d0be22abfcd0c82b56f0c3509a5afce026
│   │   ├── 39/
│   │   │   └── 48a6a56df44329bb3926644d9155c6371d6ead
│   │   ├── 3a/
│   │   │   ├── 457c761dce7367250ca7024c41c9b1f9cb1e7f
│   │   │   ├── c77e6277f9f5c15acbd852e3c10e68c1fe5083
│   │   │   └── e565db4cebf53e2bd3d533fc52a65649c0da35
│   │   ├── 3b/
│   │   │   ├── 086743faafe8390a7a34fdda0a9a4fdcd2cf6f
│   │   │   ├── 254f187851f384309e07ea186a4fafd47e2dd4
│   │   │   ├── 303904b381b720a816577ef3ba300c58dc57bd
│   │   │   └── d41a2176bfda44d5234a20be71316b18a13ff3
│   │   ├── 3c/
│   │   │   └── 0d49dffdf4c4265caf42550c5860370a5cb008
│   │   ├── 3d/
│   │   │   ├── 3aba0dc3cb3b91329069a2b7aa6aa61f2f3d3b
│   │   │   ├── 498e530b6ca9809c82313628519877d2169dc9
│   │   │   ├── ac1de828b1a4f87ec86bcb1ecd42d3a95d0f3c
│   │   │   └── d0310b0da170a9151eb43f62ae3ba38fd89518
│   │   ├── 3e/
│   │   │   └── 21045a7b25105743f26f50dce42066bd8d89bb
│   │   ├── 3f/
│   │   │   ├── 54c2653a5a2e6d27f97d8825f26b1a359efdea
│   │   │   ├── 7e7a81f0c39c31fa59167a3123c9f9a51cde56
│   │   │   └── 814bd93cec8dd8bae7416a347170b7ecb43c26
│   │   ├── 40/
│   │   │   ├── 3cd590db9d2f284e76d9d36b15ce59a4b0472e
│   │   │   ├── 673fe39bfaeab7a8369a939e927e370503f127
│   │   │   └── b5b89550bfdd796ee2b2126451003916a1314b
│   │   ├── 41/
│   │   │   ├── 09f13ba85cf556a9055c9b072d1220fe34f493
│   │   │   ├── 34856e874daa5a3372c1e0671d928c0ee8cf71
│   │   │   ├── 3be9f160b7920cd1729d2f8b4016ba8ba1dfa4
│   │   │   └── 446a7ba6138cc2e3c591cfd0a61d19902954e0
│   │   ├── 42/
│   │   │   ├── 022c214b8c47a5d5f9774197529f763d5716a0
│   │   │   ├── 15726271a8776c1e75cb0aa0381e878810eaa9
│   │   │   ├── 843b6946e69af99a8720a42b17bd005f8b9a87
│   │   │   └── afabfd2abebf31384ca7797186a27a4b7dbee8
│   │   ├── 43/
│   │   │   ├── be47b7f048be98141101ecf3e98db9e387b551
│   │   │   └── cd24aa6f1e517e155427de65489dcc87ca40a4
│   │   ├── 44/
│   │   │   └── 744dbd0b95ca7f089778eb3cf7758e1bc32274
│   │   ├── 46/
│   │   │   ├── 123c8a05f7c8ae0d069692786b4f3b1a0648d7
│   │   │   ├── 2e56032a63cb8e293ac5dcacdf86ab0f3d1746
│   │   │   ├── 977899c5ca80fd082b53942a8f7033e6704941
│   │   │   └── 9c58f6ce01166badc70e7e33ff5f5831823cf3
│   │   ├── 47/
│   │   │   ├── 6e74067f0e44b79e6fa2b788c982f8ed8e2496
│   │   │   ├── 6f92e9dc32310caa405cdb08c8ed854fbadfda
│   │   │   └── 81695c11f7ff44cedd58b5b86b970bb906c1ed
│   │   ├── 48/
│   │   │   ├── 1bb434814107eb79d7a30b676d344b0df2f8ce
│   │   │   ├── 3f8a2dab042e4add92524702e0df83f094f4fd
│   │   │   ├── b0f85bcf1f29b432aea0191719445334b8bd6d
│   │   │   ├── e735128fb896aa72f73f2a599b63e587ed71f8
│   │   │   └── fbf4d344dacb614e5dbb56363191da07186f05
│   │   ├── 49/
│   │   │   ├── 163209b79cdccca22c1b4bb837e397424fac1c
│   │   │   ├── 68f3e367e215d6ffe06e8d138ac5f0018e3243
│   │   │   ├── 83ee44c23c8e1ebe60d5c92f7dacfe52657ed3
│   │   │   ├── 8aa6b6b5727e6c038977bd4c3d951cf51c4053
│   │   │   ├── 94127eb4a987332462c3f730f8d6086248c2d7
│   │   │   └── b3f9109f6d7de2f302d2fbd8cea34cfe958354
│   │   ├── 4a/
│   │   │   ├── 0b87cb016471e9525d38e7ed4e47927b790311
│   │   │   ├── 283b9c2384638a6f812831712d5bd2026185f8
│   │   │   └── 2fd661ec895b786affc09bff233d5ce451e071
│   │   ├── 4b/
│   │   │   ├── 112e188d47c276882ca69eed511f79f0b7c902
│   │   │   ├── 3a271914117a60d67f083aef8756c90273a167
│   │   │   ├── 3cd76c9bba78b875cd72b16b9e73aa77b065f4
│   │   │   ├── b3afc99d463a600c1620d14be829433a4849eb
│   │   │   └── d9ed6e8fae3402ac1500711cd93c2a781f16d4
│   │   ├── 4c/
│   │   │   ├── 223619ddb60a303eb2a0d249c5ec1f9a99862d
│   │   │   ├── caee1463138f7a779db5d8990e312472cbd633
│   │   │   └── d2c643c37a3f2012416117fe3bf126bc69179e
│   │   ├── 4d/
│   │   │   ├── 252647017a64741c1a5732b0f050eaee511961
│   │   │   ├── 5b833e0b5cc5738fe6e43724c90d54f5077acf
│   │   │   ├── ab7956caad44e984a08dac1314272b2b77f07e
│   │   │   ├── d0dcff2e7c7965a6da6fccf208446fe967350b
│   │   │   ├── ed0f181cfa0989a59f15f3da11f963ee6ca8e2
│   │   │   └── f925582478cf0d469c4cff9a4bb50859f3230a
│   │   ├── 4e/
│   │   │   ├── 1fdac17375a22bfb0465ca9ed6edaf239eef5c
│   │   │   ├── c50203ac15eb0b6070972d5184779681cd6820
│   │   │   └── e45daa93093303208b62887ea3b0043dddb939
│   │   ├── 4f/
│   │   │   ├── 0f1d64e58ba64d180ce43ee13bf9a17835fbca
│   │   │   ├── 15a5c88bff41f076fe39b292f1a8c413fd4bbb
│   │   │   ├── 4ab3ca16c106b7ca42fedb761d254e2bcbdc80
│   │   │   ├── 4f2da0c86e4eadaa8f0717974cc94a663090a1
│   │   │   ├── f439d85e10b5f76cf041dc94d8bd814c9e6f43
│   │   │   └── ff60c22972c06f0d5dd246b80d14d5e678cef5
│   │   ├── 50/
│   │   │   ├── 52c37779a7b6b979e04296525ef4b9fabcbd09
│   │   │   └── de07959361eadaf06a17be556adf6b45da3255
│   │   ├── 51/
│   │   │   ├── 462696b3f51f954451b73205d181aa6ad76c4f
│   │   │   ├── 6abe05699b4a4d708524ba0d8a6ebbd734ec1a
│   │   │   ├── cf7ad634b803bca0435b258ff88a4022b5b8d5
│   │   │   ├── d9bf9bb7eea0a47fbfee07888b589ed5a90e5d
│   │   │   ├── df199ac1a74f0c9bfc22e1b8ab9d0cf8a650f5
│   │   │   └── e0a841aaf13d02e2b83f09021f2689966d4920
│   │   ├── 52/
│   │   │   ├── 7826f864d0731f6751bf8e43ce2cf3192301eb
│   │   │   ├── 9a701c7540fafe4f4c4da136a1a81574c85532
│   │   │   ├── cf5717eb80aebeac655ed2efed3feb8c6f3ac5
│   │   │   └── d68a9e9611c5b1865d33ca9e498368e503fc14
│   │   ├── 53/
│   │   │   ├── 25a4326c3d0a7667d457412d25e3af85bf9716
│   │   │   ├── 483786500134f8d9c0839fb746985393b9d779
│   │   │   ├── 5544a2b05d8cb32525ef26cd3597cf6ab3d878
│   │   │   ├── 55e5753115772245b047a60c7d0d420bd1dac4
│   │   │   ├── 5b8c9bca9ed55e5080d54625ec4f70d484d321
│   │   │   ├── 617c31b91c3c4f109d0669f6573d41e090e18c
│   │   │   ├── 67915e193cea4e909660f064a181788d82f9dc
│   │   │   ├── cb4b42bd4059d91c159a1e178e84abe6982c29
│   │   │   └── ffbf5fd863a95e7029fe8f4487ae7772e3afdf
│   │   ├── 54/
│   │   │   ├── 0dd78e335aa79443d05a965a4f7c0d0dea31f5
│   │   │   └── 523a175b6d39393c59b72f21b33e905c9af9a9
│   │   ├── 55/
│   │   │   ├── 2a98e802821bc79153c1a68ab6be8ab91cbb7f
│   │   │   ├── c6ee6f9d7834e15321428306749a37795f11a1
│   │   │   ├── c723afb9cce67ee03b6d66e722b0f9fdb40cb9
│   │   │   └── f714f79be63192c5e0f5ad5a8a823161cf7f7e
│   │   ├── 56/
│   │   │   ├── 0b6f0a475464dcb88521578da51d4f99dee784
│   │   │   └── 44530ba083917aab02e89725328e0f8069419d
│   │   ├── 58/
│   │   │   └── a516f2abb24fa3cf08626e04c7363a09029167
│   │   ├── 59/
│   │   │   ├── 0830fb86ee3e4bcfb2988f3dc70119de4bda58
│   │   │   ├── 1ef28a181d1e46a344c01eeec9b6ccdd3954d7
│   │   │   ├── 9a2e7d2f6aa42d055c0d6dc5c285b1a6608619
│   │   │   └── c084ab469d0ed2378a6d64680110a6befeb27c
│   │   ├── 5a/
│   │   │   └── 6ae7dbe31f4311b240de61a09e39c985361a5b
│   │   ├── 5b/
│   │   │   ├── 742344dd8cdfbe8bc64e69a5bd57190646cc43
│   │   │   └── f9a7620cf86ec34ad322341446b640eedf1553
│   │   ├── 5c/
│   │   │   └── 1033c82d5877f5115531c977d00d6a368be90a
│   │   ├── 5d/
│   │   │   └── f3d91fdf78f6e851a92a444df09c5bd6ebb51b
│   │   ├── 5e/
│   │   │   ├── 13ebd1cf9cea9c6d34c0a02b7a1f9511e2c582
│   │   │   ├── 1b42007d515c35de0dc80358f275a55ceac6c8
│   │   │   ├── 4c2c1cc72f396202ea2ec327fab5cbaf6ed18b
│   │   │   └── f55e2eae91b27b606b0582fb65623650a838f4
│   │   ├── 5f/
│   │   │   ├── 05f654c43a9d6f32b9efa15814a9d68a1c50ac
│   │   │   ├── 0a10d2d9bfe93baa962e4b3373d5349145da6e
│   │   │   ├── 1d72a13f6ad6eed9f21200a83c6ae03bc8defe
│   │   │   ├── 2b1de3601ed3008d04171d44080bc388740ae9
│   │   │   ├── 74d0d299c55df0c91c93ebaafee3db0eeaf050
│   │   │   └── e68495fc1799f5df172d873ab97df771dabf7b
│   │   ├── 60/
│   │   │   └── a05672ed1d31d562bb924e1aa054f100d4135a
│   │   ├── 61/
│   │   │   ├── 13ba562651ffe7f4cc40f88b1891b19c133031
│   │   │   └── 8421bf28c62ed4458ce90945c4d114025249bc
│   │   ├── 62/
│   │   │   ├── 15a0eb0816270a759c4a4e85f9290f2d23e62c
│   │   │   ├── 9857d78d889d30d52f4ecd9101d3619b6f87f0
│   │   │   ├── a83a668d8db38a609000d2ec6c7e3f94ef8651
│   │   │   └── b611da081676d42f6c3f78a2c91e7bcedddedb
│   │   ├── 63/
│   │   │   ├── 141ef5dd3fea4ac94319aa66c3a5894db83876
│   │   │   ├── 4ff9a78483e40ee9286b86547c5b1763f5205d
│   │   │   ├── 5f6cc782e41b30110757d00a4eb54044f973c6
│   │   │   ├── ab31c2bb1f8175b0826caaf9f7b051e9211915
│   │   │   ├── b4f0e49e0b078413faf0add4023d499fe95191
│   │   │   └── fc774f9219f029ca25becd3e56d1ace46baeac
│   │   ├── 64/
│   │   │   ├── 016f94114b156451ac374b2e21461ba88ca312
│   │   │   ├── 4eb305888b089758cffb3108f819f9fcfbda94
│   │   │   ├── 9659d872d5addfd0dbf614c498be4437bfbee7
│   │   │   └── c38b23e1a9e1afa68069a79541015593038922
│   │   ├── 65/
│   │   │   ├── 00863c43ce626e0b3c292049e348d442f13bf5
│   │   │   ├── 4570b2c2988bf78df9da650dd85721140df726
│   │   │   ├── 51da1dbaff8bb82b5ec88bbbc837d55e86ff00
│   │   │   ├── acf75dc55a8b9dee74c994cb10e974c65a60e3
│   │   │   └── c6ecf8c9b215f593e58b544a4c36ed45ebdd1d
│   │   ├── 66/
│   │   │   ├── 31af214ef641a9b0df20498b060556daedc1f2
│   │   │   ├── 6b912caacce4e7bc78896cfac9da10a8e3268f
│   │   │   └── 98127317e1292d4a3641e019e4721ef813ce4d
│   │   ├── 67/
│   │   │   ├── 7489d71c6321249f03af2b2fbc0ab9ecef9395
│   │   │   ├── 973e5542b7a39a2bebb5fb44f0633f335ec112
│   │   │   └── dc48f223af73d9c27935ec1b0c79b23ba3ba12
│   │   ├── 68/
│   │   │   ├── 2fbc45e1bd9af5e6e58585399a3b238c0785c3
│   │   │   ├── 444b773a7d51cd21995a35456045c2dc7b9c0e
│   │   │   ├── 9c12c359d98997902edd9d36d81b5fbe1ee8f7
│   │   │   └── f5b0c04d04ee5e529c6eb9003889ea540721a7
│   │   ├── 69/
│   │   │   ├── e2e0a6305e571d0532ced9288e2158bd65ee93
│   │   │   └── e303eaf137eab3417486535a201a29cd36e723
│   │   ├── 6a/
│   │   │   ├── 2cab254cea01c256879a5e844f7f46d5a517a2
│   │   │   └── e98632dff77dddef8b6b1191008524f0ad93eb
│   │   ├── 6b/
│   │   │   ├── 2c567943ab916ecc2714c4e24b9f936760706a
│   │   │   ├── a2a7139e762970a4237566038c909713946697
│   │   │   ├── b040af5fca5a135547b8bcf944197b0f4104db
│   │   │   ├── ba46dd69a3ca7d8c411d2a78a7cfd3195ee05a
│   │   │   ├── cd64b70006807334933404a5ce053b3f97bf73
│   │   │   └── e554c9044c2334324177889c384ee72772b117
│   │   ├── 6c/
│   │   │   ├── cc79bb47df7ef77e30c309aa3e645c725329e4
│   │   │   └── f191a9a51bdfd72ce048d0083901035fe877df
│   │   ├── 6d/
│   │   │   ├── 392dbb7df6a22599e0380785bf9871864ef167
│   │   │   ├── 49a45effe54c41b41671bc90b387a039341a2e
│   │   │   └── 58055c9c881f360eeecd69cc880d21f22460e5
│   │   ├── 6e/
│   │   │   ├── 7f095f6947a73866aff319c69132088022ace2
│   │   │   ├── a760c3fdbd450bcf0bbebe3ff8b573ed7df4ee
│   │   │   └── e7ac2d2814f3a9e926d1af7012f79d9727d648
│   │   ├── 6f/
│   │   │   ├── 3b755bf50c6b03d8714a9c6184705e6a08389f
│   │   │   └── 8b3555b69b26aabeaef93acd72a8898df44db4
│   │   ├── 70/
│   │   │   ├── 1e3056688cfa577a905027e07fe9186f2b955c
│   │   │   ├── 408018369c1a7fd124e78751de169b9432394d
│   │   │   ├── 42af0809609a57205aae47794045ca1a17e4a7
│   │   │   ├── 43c71a78773f71a7f6100fd84d0b92cf461585
│   │   │   ├── 5664f17465557f6360d3d8c09d0f1a3d9adfb9
│   │   │   └── b78ed3473d22c24e016fa67c14f3f47108c21a
│   │   ├── 71/
│   │   │   ├── 81bc3b0f4c60006ea3cc40198aa7b1f4d9306e
│   │   │   ├── 8453b97b45aa48a4a92f6a4aab8013bf64caa2
│   │   │   └── e7dae0de8b35ff44db239f3eab1b189e41fba4
│   │   ├── 72/
│   │   │   └── 70cd3f73cdd43f15ce2dd7fce28a24b0c6f4a4
│   │   ├── 73/
│   │   │   ├── 746c1e739ecba9e71bb332a4a3e21791a42ee8
│   │   │   ├── 8cdb43eb7f1dcbcc03e7724aff6395f80b1591
│   │   │   └── b18d67c5777e523720a8f09bed8a0a3d7b6196
│   │   ├── 74/
│   │   │   ├── 5545c707b9f2f9fee1fb1bfa5c653a59dfd9b9
│   │   │   ├── 618f25c88a1da1b774dfc9c82b650b6521f0fc
│   │   │   └── 68371af6f5fabf901a44456160d07d155999a9
│   │   ├── 75/
│   │   │   ├── 210250a21c5d1bf1379f12118ec865d1031ce9
│   │   │   ├── ed03127633550d797d27ba59593f0a4b786bee
│   │   │   └── ef358a7bd0a34597a4337d7bd0fd0b3384feb3
│   │   ├── 76/
│   │   │   ├── 86dc46621a547ba4ca3ab8945d7fa0c52b70b6
│   │   │   └── e5f9702473cbf94dbaf96bea8443bf98ae41f9
│   │   ├── 77/
│   │   │   └── 1a71b598ed4ea33b3a66273923aab29fc5970e
│   │   ├── 78/
│   │   │   ├── 0bd20e6239498f4418f2558a8680d0685070b3
│   │   │   ├── 263618959a9ae9a00e153ea8ce0dbab42ad207
│   │   │   ├── 39428077e011e78c5b941e4b2c9741a9bfc0a4
│   │   │   ├── 3efb29fa378e37433aee8337865de5366c8795
│   │   │   ├── 6ae1f3a311f95e95ce47702dcfd9e9ed2cb27b
│   │   │   ├── aa361d16f2e13936ea7578d91f05f47f4ba0d5
│   │   │   ├── b85d3222ae89f8ce0e0d26dbf271e475c64e61
│   │   │   └── c075fbb0a1fa177e86b4180667d1318ff28128
│   │   ├── 79/
│   │   │   ├── 2a67eaa026f986444e914e379eddd4f88598b9
│   │   │   ├── cffb6dbbc30cf169540531dea8a64ae7cea310
│   │   │   └── f023976526174362d02170ae13608536290523
│   │   ├── 7a/
│   │   │   ├── 4cf67c91cb9749fb7d3dd208092764336050c7
│   │   │   └── b864ce708afbf07ee807157718fec660e3dfd7
│   │   ├── 7b/
│   │   │   ├── 3182ecf904294e3809b11e6cbc326c81579c65
│   │   │   ├── 32e49b6571fdc299a3e9cba89fa4218ccf1790
│   │   │   ├── 35d9e327ac21e62f233575209892368180279d
│   │   │   ├── 5e7945557d357e8b07a20fea03c9489ffa3aba
│   │   │   └── 689ee27698e40807c7aa1b474df8166a164f76
│   │   ├── 7c/
│   │   │   ├── 262462995e07d753e232ff3b19a20c3f5657f4
│   │   │   ├── 47d2811f2db1aba31e8d6d0b25d179eaf7318e
│   │   │   ├── 56a8989779ccb53057c9af7971677cd10622f4
│   │   │   ├── 608e6b10f1cff56611eaef0a19c88be54aa087
│   │   │   ├── b5ece5e88b1f46a53de59496948ff354ce6229
│   │   │   ├── c0d0c4820db2e505f59592b20cd2b7199e5323
│   │   │   ├── c3d80be1315ce5949be53d7cd50bc78449bfb1
│   │   │   └── d3a3f2308c9efd12d937caa74dda659c6810e8
│   │   ├── 7e/
│   │   │   ├── 24cb8ec0ecaf333f470e3a38b83177a0dd5adb
│   │   │   └── c1db6861966dc582c1789447212857ffad5c26
│   │   ├── 7f/
│   │   │   ├── 1b5c2b4324279733ec55748f016af1a98c1bd3
│   │   │   ├── 4e061b657f340e82074e4f16026bd97a7de92b
│   │   │   ├── 99c3b3e52715a6933ecb6539f2b2351008c5e9
│   │   │   ├── 9c6fb7a56d4194ea47c9df1b8fa323b5d161b4
│   │   │   └── c135d0c0e48574f521489e3eef7d220e968939
│   │   ├── 80/
│   │   │   └── 5c905499e73c9f5c681b235d6358efa4a7914b
│   │   ├── 81/
│   │   │   ├── 359f630299bd4ede38337a313dc3ef77f8e1c3
│   │   │   └── 8d172c19554ad8d889966a779500380a0524b2
│   │   ├── 82/
│   │   │   ├── 083f49c785dc851403b018a22fc61ad2ba689d
│   │   │   ├── 8e507dbec0b9ec3a0fd50d7f472bd9e430c22d
│   │   │   ├── bb0c89ebfeb031be0c05fe48aa174820afd233
│   │   │   └── fb518c8532c606b48b07428bbfeb0022b2b2d7
│   │   ├── 83/
│   │   │   ├── 016ca46d185787231370e17aa5d7e4a5a5cc31
│   │   │   ├── 620deebbc072ec41f54368bd7251862a1b476e
│   │   │   ├── 6b20a15f85ba62cf15cd9ac7e3c32e8a5e3a1d
│   │   │   ├── 6ec4e3246574479d046d5651cf6ea2a8f7fdb2
│   │   │   ├── 742fbd3facab1d9de2a42943b83d860b8eac4d
│   │   │   ├── 9fc83bb037fa29fff915b7a88e2ee5d1df86c1
│   │   │   └── c461fbb1cd88297c98a4621db41c74b2762737
│   │   ├── 84/
│   │   │   ├── 0e318ffd51f81aeebf9b41772f3b3710f41a70
│   │   │   ├── 6cbb1a89a6ff3c6a7efcfcfa6364956b1d3f8b
│   │   │   └── dcfd40e2a28d706ee0db47804579f3e6609747
│   │   ├── 85/
│   │   │   └── 9246310aa33d46d04c77ea04abb2d3e08e9c77
│   │   ├── 86/
│   │   │   ├── 3fbf1ded8e52d482f871e23b197788ff1a5522
│   │   │   └── c3024d0309f6a63877965549679d2c692c36d6
│   │   ├── 87/
│   │   │   ├── 5e0c35f38c02757cdd9980027cdb50a6bec712
│   │   │   └── dc9d9cf0a93426b8110a4efa73a9580e9a3712
│   │   ├── 88/
│   │   │   └── e089c6ca471eb6c11426c1011b352a7bcb9d46
│   │   ├── 89/
│   │   │   ├── 814540b70126b04212f87c6d2cc252bdb62c0f
│   │   │   ├── 9eb4d14d6faad330e6c154ef4b4a942a3cbe46
│   │   │   └── fbf3f666f01cc2c40839d52bce155eabc6cf5a
│   │   ├── 8a/
│   │   │   ├── 33154c123abe4ed221f6b873169cdcd7ab980f
│   │   │   └── 7dd2bbcdb241a465fe10da39ea55887d87731c
│   │   ├── 8b/
│   │   │   ├── 5e4ce175a15ecc2c029819d026215926866c55
│   │   │   ├── daf60c75ab801e22807dde59e12a8735a34077
│   │   │   └── ee680d56efc9904a1faa591ad80310b0a1d269
│   │   ├── 8c/
│   │   │   ├── 2bca619972e10b647cfc3655f70fa8e58bc356
│   │   │   └── 3891aea5a1e9caf470ab6924ea15e0c7c1b618
│   │   ├── 8d/
│   │   │   ├── 2cf7d7230001c6be25309f124de0f97900f2a2
│   │   │   ├── 3286a6c7daa320bc30ac3e601305eade75a532
│   │   │   ├── b8e7a7f1f3df649d1f713cb9f6de04ef8ea564
│   │   │   ├── c7a720d5fdb227c7bbf3ef3d97896166c8cae0
│   │   │   └── d4315d83350e48ffdb3c27b0e15c72a338d93b
│   │   ├── 8e/
│   │   │   ├── 135b73d1a8b22edc8f01142f44846d875d110d
│   │   │   ├── 1eefc44bfb0931ccea9f76ef52f68319f5cb2b
│   │   │   ├── 88d9253475a32b9a150dcdd15bc5184a0ac758
│   │   │   └── ba9bf0d2493c2a25b14e93f739bf7fef91e50f
│   │   ├── 8f/
│   │   │   ├── 5953683d1b1322b20a8e1eba72e41a742d653f
│   │   │   ├── 632273893ccc4cd240735ef27bb097aaec3052
│   │   │   ├── d4ea4d07d062449c54af0f5c76fb43bd1a004e
│   │   │   └── eab97b7bb4582bd7421988ae95ab033ba2814b
│   │   ├── 90/
│   │   │   ├── 2231e699c0e2a1ab65216e0439e9aaf38e1c6e
│   │   │   ├── 3fe279d5216ee579f8b28cc67d88be7e421243
│   │   │   ├── cb0e176abc859a488c6ddd8163e0edfc91bfa9
│   │   │   ├── e0263c36ea6cd95e8c16d45f7ec9804f7c62fb
│   │   │   └── f36d33993b598967799dd3e80c9f92d3fde9dd
│   │   ├── 91/
│   │   │   ├── 26ae37cbc3587421d6889eadd1d91fbf1994d4
│   │   │   ├── 9fcb2c40c7668bb70e7314ed1e4a45b382f8b0
│   │   │   ├── a75679dd13939b80684f7188782528ae60f20b
│   │   │   └── dc1f23229c2dddcff8c824423e37e2ae64dd13
│   │   ├── 92/
│   │   │   ├── 87f5083623b375139afb391af71cc533a7dd37
│   │   │   ├── 880dac5d170008d605ebc3f850665e9d506ac6
│   │   │   ├── a2d46cfa6545b8ba5c1e6caf9f416fbdc6ee81
│   │   │   └── ab1e5442e55ddf015e4c9321f5d55e3abb2fd3
│   │   ├── 93/
│   │   │   ├── 23bcd7ac702445ff9fd2339a81721375b5a9b7
│   │   │   ├── 6e52cb934f6bd49a0168e00588ccd641fd6427
│   │   │   ├── ee1345a2117ceedc9d42dd3643252a0e80188e
│   │   │   └── efef4abbecfc909dcb82e79e6ee25f767b68b6
│   │   ├── 94/
│   │   │   ├── 1e5fe3c334377184c3a5a17bd8090d4bfa5a2b
│   │   │   ├── 74a2326aaa26f786019c050c6055377773d1e8
│   │   │   ├── 7c2f4481f2c02b7a83bd71077fa95bedc9aa63
│   │   │   └── 8a3070fe34c611c42c0d3ad3013a0dce358be0
│   │   ├── 95/
│   │   │   ├── 08dc1c52b56500f993d080507fad945444f6e2
│   │   │   ├── 7d39918159d8302a94a33fd2207dc01d87d5e2
│   │   │   └── 8df6a1a0772034a5e1a240deb08393e92a4c62
│   │   ├── 96/
│   │   │   ├── 238b199aeb2b4a7cbb735129c4d8ea1a9f9695
│   │   │   ├── 4fe73e2dd6f2c928666cfc60a0bdef1b0fab9e
│   │   │   ├── a4e9e2021c44e5c920edc48ef875c5fc6f1eb8
│   │   │   └── aa0e4b3aa697368b7e3ea8dac54b5bd87595e8
│   │   ├── 97/
│   │   │   ├── 50b09fd8c0b9f29798bfed8240f44b2870f574
│   │   │   └── dc8b563000c19c3b4a756ccaac7bad55af873d
│   │   ├── 98/
│   │   │   ├── 5b626c973bad329b4f5c89d39bcf30819289da
│   │   │   ├── aaa95be7233f3563bff365acb68d42d23ac9a4
│   │   │   ├── b02881f3cfca81ad9b8e347e5bb24401cbba1e
│   │   │   └── d0d00da84d4dfbf355e0d1543c4766a8ebbebf
│   │   ├── 99/
│   │   │   ├── 0c6174b946b0d014da1d7eca4205bc4e0a231f
│   │   │   ├── 2b12c1efba24fc6a821fed4452f56b5b84bb96
│   │   │   ├── 50969702f3e15fbdd4048d71d16211128a3000
│   │   │   └── 63e4738070e60ae1160c53a48b488062d13432
│   │   ├── 9a/
│   │   │   ├── 40119c006625aa527b3aea53b67648c03a48dc
│   │   │   └── d82b82e0c177c0dfa61ea80b7f2fb53e631320
│   │   ├── 9b/
│   │   │   ├── 168896e301b3c3016bee7423b67fb440c1ce7c
│   │   │   └── 910b71debe3ec39a498df5ed1829383372e813
│   │   ├── 9c/
│   │   │   └── 2a513ab4257552a3ba794c725b25020d609626
│   │   ├── 9d/
│   │   │   ├── 0f93e0e9877695934cb73a2c82829b53c568ee
│   │   │   ├── 317d578a2b5e5e9029863ebc736e3da5c9bc70
│   │   │   ├── 3726736f16e9ff475ecb0cb89e5c0d4980881d
│   │   │   ├── 6a9270d7eafcc02fd054779e8ccd86bbe3b533
│   │   │   ├── d12428a5821df8fdd1a93b61ba35be30e399f7
│   │   │   ├── e0efd4dd30bc75e3b1ce79974f29af53b118d9
│   │   │   └── f2c751a34aaf864a96e88064b15d8e80f129ef
│   │   ├── 9e/
│   │   │   ├── 09de06f2dfa85f89946dc55c8541e92efde2df
│   │   │   ├── cd117b4b016aca1d43a6f613236eabba3c9cb1
│   │   │   └── e9997b0b4726e57c27b2f7b21462b604ff8a88
│   │   ├── 9f/
│   │   │   ├── 3457cb5b86c657b9ff08f5a1cf062013688526
│   │   │   ├── 7faf1cc8b801bfa7f603e789de9169b4af91ca
│   │   │   ├── 9dc17208b01ca7e875717a51e480fab6c2c4c6
│   │   │   ├── b206c634820ece0ca17afa2dd69dd68f9c4d3a
│   │   │   └── f8304dee9d7a33f9736597ed42d458045ded5e
│   │   ├── a0/
│   │   │   ├── 364cd71b6e9fa7bf6969a0a77709ce47b9dd99
│   │   │   └── eaa5916cf78d45970b2fa11d6d7d7d3186d0cb
│   │   ├── a1/
│   │   │   ├── 54faec9a78106adb734963cf8e629a927cd6b8
│   │   │   ├── 69c31ac279fbc1fdc0396ddd7fb8afdeb99fef
│   │   │   └── c244ecff89ecd965d9324376e5b7ec19465eb8
│   │   ├── a2/
│   │   │   ├── 6bd9bef524437531e9357aca78e46d5d4ed513
│   │   │   ├── 9b6a99a47349982989fa7aff5a29177a6f0985
│   │   │   └── 9ee59df6555f663bc35e2de7b5299f6860aeb2
│   │   ├── a3/
│   │   │   ├── 71ad66bb24e81c0f8a714349b8c246447d0e8c
│   │   │   ├── c3fcb53fffa70b7ae326ffaed67e4192913112
│   │   │   └── de34961096076e3ad578310142d8e33859d1bb
│   │   ├── a4/
│   │   │   └── f846114a28f37795614b460f351d00e6cc1551
│   │   ├── a5/
│   │   │   ├── 08d86f03eca46431a705d1b674b6958c93a4eb
│   │   │   ├── 24908710bba9910c1bec8084fa329596fdc703
│   │   │   ├── 2a9e4641f491c46d4abaa50c6c9751b5a01c51
│   │   │   ├── 4adcedbd4d7c0455f38320802fc6f2e1e4bce4
│   │   │   └── c088752dcfd204f483dc073247646e593ec915
│   │   ├── a6/
│   │   │   ├── 0e1fab85e63455a0551346e977e4578c239ee2
│   │   │   ├── 3e9ec9fcec0c4190c308d98ed986375d1a8eab
│   │   │   └── b0a9639eb680d15f37630a3fa635b002b954ac
│   │   ├── a7/
│   │   │   ├── 5028be8da919c9d10ee0123a819ebd75d3ed79
│   │   │   ├── 9623a069fa6d6619a2a08307fe6da8f079a0d9
│   │   │   ├── b3a3a522482b52f3607d7449f921f474fc0594
│   │   │   ├── debecca3092f6ad52e2fe5949fa54259382faa
│   │   │   └── e65f5c282ba5a7e0bbf9bf32841df7a06beaef
│   │   ├── a8/
│   │   │   ├── 06734e2176ed0a540dbf2c5ef716af42348785
│   │   │   ├── 2807f42d8fd7ac351ab108aee79e7930117219
│   │   │   ├── 2d6e0415b1fdc259f3639b4fb0392c838af4aa
│   │   │   ├── 3fb9bd1064bd236279de4b94f56e2a629c68db
│   │   │   └── fcdef88e7b8b4ccb48df7be911d37b34a3d2f6
│   │   ├── a9/
│   │   │   ├── 006689a1971e8dd85d77cf33f8b0234332ffac
│   │   │   ├── 04e1d79389c8d47c2b0387dfc8d1528fc2ab2b
│   │   │   └── 5566a74bdcaece1c68c13c8d04f2122fe4af44
│   │   ├── aa/
│   │   │   ├── 4128f4f0f2858a89e4394d524a9f8e242a8275
│   │   │   ├── 4b3f3272f9e2177cf2ad57bde342df6391b797
│   │   │   ├── 50b12437123a158992b0ebdff9da113fd44747
│   │   │   ├── 7d6427e6fa1074b79ccd52ef67ac15c5637e85
│   │   │   └── a16c4e2f856a1a2ea7ebed0f28a84cc3b4d18f
│   │   ├── ab/
│   │   │   └── dfcbeb96fede929e71379182dfc539d84b11cf
│   │   ├── ac/
│   │   │   ├── 88ad6d9d74db0fbcf64b53471c6ae6dee79186
│   │   │   └── b503556ede88d9114b3b56f2eaf36531c2cb46
│   │   ├── ad/
│   │   │   ├── 02437f226e3449019d99aecf61ede3fbf7795d
│   │   │   ├── aeacd0e4d5dcebe67eab10d9fa0b27059669cd
│   │   │   ├── bf1e2a7b11d938624100f797dfc4e99aa9b41d
│   │   │   └── eda5bd99fd689628a80975533fd8b3a82fd536
│   │   ├── ae/
│   │   │   ├── 1d4f1c070cc39f60ddfe6c8a8e25907124d98a
│   │   │   ├── 418d80187ea2c8f161d7c1eae9f96e2392c05c
│   │   │   ├── 99004b2d5171152519915cac69f46ee31fecf8
│   │   │   ├── de61f9accdc6beadda1f10b9ecf3deb7f6cc54
│   │   │   └── de97bf1845fc360110cfcb84e0c57cb3fdfb83
│   │   ├── af/
│   │   │   ├── 8e732a262059c37da6f505fa1e98bc7d33a30e
│   │   │   └── a96dde1a3192b0873a6fc10f971454f56dd573
│   │   ├── b0/
│   │   │   ├── 39be7537667fc0e1a5aca26962a2bf40b58052
│   │   │   ├── 3ce8fec3bdb69e2664241f99dc05b75cd24d10
│   │   │   └── 93d580d0c56fa2b332b095442869b75a89a3ae
│   │   ├── b1/
│   │   │   ├── 105b779e59fec29cf94d0bcf2e54d9e1e6bf0d
│   │   │   └── 251ce1c4a8c6daf3f98dfe2c255f3ffc2ee111
│   │   ├── b2/
│   │   │   ├── 5569ad78ac6caa08e40dae0294a511e8f1d93c
│   │   │   ├── 575503d5c433e902790f2d9a51bd7c56bea930
│   │   │   ├── 80fe733130c53949a43354344ed49d3664119c
│   │   │   ├── 926dd93ead352348fcee9c5b8e960076e36dbe
│   │   │   ├── aa8345cbc762adbeb3eba03a499a936e37bae7
│   │   │   ├── dfe3d1ba5cf3ee31b3ecc1ced89044a1f3b7a9
│   │   │   ├── e15a31bff495022e4f0caaf04410993c4005a1
│   │   │   ├── f82a84e32ab0e2c3a24330053f2e47997b7ae8
│   │   │   └── f881b7ba36d4c5d1a7a3228fa7467c096cbfbd
│   │   ├── b3/
│   │   │   └── 9f0035a8cdf50079111f3d147e11e9082ae55e
│   │   ├── b4/
│   │   │   ├── 22dc79fc7c117229ff1bbb8bd3344c31195cbc
│   │   │   ├── c86e07d49fde11dd657c89166ee3028bd175f7
│   │   │   └── dec1d1d928978672427e163975f8554de20b2c
│   │   ├── b5/
│   │   │   ├── 7a440c8759969cd3de20525a002318b272c979
│   │   │   ├── 7befe9d13a1b542cadf1ac5bc0989525143aed
│   │   │   ├── e584c1be7fa8e623ec35d1a6858820d3277163
│   │   │   └── ee780a8bf29cd7534c9702ea4133199d51784b
│   │   ├── b6/
│   │   │   ├── 261b7acb03fe0fbb657c69fa1615535d7d71de
│   │   │   ├── 3b4530ec6540a0cae3acc9b50061280e183956
│   │   │   ├── 62d740a8217c9387eb2492c7d32315a65d1a2a
│   │   │   ├── 670ef2671c44dc654c7d96d8737076b5326ef4
│   │   │   ├── ae21979877e86b55707c9e941a9bd258dad9b2
│   │   │   └── ed4d76358ebc3cad87667f13c911508cd3b206
│   │   ├── b7/
│   │   │   ├── ce6b2195111977bea13ddd8a4b6131543fb6d1
│   │   │   ├── e71eeca68a4850181b973ed4a5baa0b1adaadd
│   │   │   └── fe870f371a6d593a593264cef5630c40a99437
│   │   ├── b8/
│   │   │   ├── 29231d96de92a857342f24dad1103f6a2a77e9
│   │   │   ├── 41e7fe63adbf68bd5b3493c6e46d1fc7f3d45e
│   │   │   └── a4dcb425d0e951019a78fe848621858022699b
│   │   ├── b9/
│   │   │   └── 2424f5c207efb3ab6af32e725c2f86cdd7fcf7
│   │   ├── ba/
│   │   │   ├── 6996e0dd7390787033955d99f925269cdff7a8
│   │   │   ├── 6c0acaf70c4fda6970d3f4c20dd5569968c1ac
│   │   │   ├── 7dbde255717f10143325b65992d95ba155bfe3
│   │   │   └── f3268064527bfa1f45a66d3c5ce0083e52df7a
│   │   ├── bb/
│   │   │   ├── 6babf4b9ff93876877623375d415f998cebc98
│   │   │   ├── 6ca4f55f3120a9d49ddae3e111535c0c523c3b
│   │   │   └── b9ef480e92891a1168c89ea8e96c3a8311737d
│   │   ├── bc/
│   │   │   ├── 4f8349f22108a3a6d63656c19c1827567b164e
│   │   │   ├── 6b724a9e0675158bd119272657402fda60be6a
│   │   │   └── bd69225aa3c7ed758996d01d088c371085771b
│   │   ├── bd/
│   │   │   ├── 7ceb60c34167a477c062485fd551a273ea7ab5
│   │   │   └── f5003bdf590e1bed18bf23d30b0bf0a5af0dc6
│   │   ├── be/
│   │   │   └── f9fcb5f91cde0e22b458c1feee78ea1513d348
│   │   ├── bf/
│   │   │   ├── 0657e74ea05e0d9a4df189ee7aa59ea7e4b9b8
│   │   │   ├── 1d5968ba6148da6fcb18cc026865d94e80e8b9
│   │   │   ├── 287a1fb61dabb2bb4d9f550365d78a9b07a7c8
│   │   │   ├── 3c973619b21826761deeafb2045e637c10910e
│   │   │   └── fddd501f69d5707e6d6bd2218168aba652be9c
│   │   ├── c0/
│   │   │   ├── 33b6b2e70a2d6993c415f583601bc6745f63fb
│   │   │   └── a5c201f23518f6a4f50ed2a565ab147963997c
│   │   ├── c1/
│   │   │   ├── 2324699053fcd9daf90a35d233f3409d8762d4
│   │   │   └── 7fe8b7670e70395171d7565c77fc112371c15a
│   │   ├── c2/
│   │   │   ├── 09e78ecd372343283f4157dcfd918ec5165bb3
│   │   │   └── ffb7429a51709a02ab846146ac610d547e0dce
│   │   ├── c3/
│   │   │   ├── 53dc18335e090d21ff968b2027d845d4f467bf
│   │   │   ├── 9371b434389fe33026d245a0ae9dac8111a16b
│   │   │   ├── 9e4d871595e19bea069b90f747b2a4e4f43875
│   │   │   └── a8f6715ce46e6efff550d72f5cf0b983f1232f
│   │   ├── c4/
│   │   │   └── 7463b44dd5b8cd739c3e1c81185c0a88c9da18
│   │   ├── c5/
│   │   │   ├── 0f22f8d86e498e0e733b650b28a9b94797d764
│   │   │   ├── 3f2b887476d618e8a5bd8b12cae358197c80e3
│   │   │   ├── 52b5a1fc16cae9ed97128ede9567dd60026bbe
│   │   │   ├── 62d1e44622b27cb9da5d77de2a7cdf4fc3004e
│   │   │   ├── 8ab5a6adb62315fc4f7248d971aeb71b8747cf
│   │   │   └── d79e374f28edb2d44b9a26edf4749771e094d8
│   │   ├── c6/
│   │   │   ├── 454b97efc6fc57dae87344275e93a1658607f6
│   │   │   ├── 6f6b751fe6f47fc193f6549b0ef2146d5ff0af
│   │   │   └── f801f9695cbd771a3acc1c15d59dad51970f35
│   │   ├── c7/
│   │   │   ├── 6411ea132c0e66d56896025fae29b8d653cc47
│   │   │   ├── 6b3554588b68eff5ad3b2caa62f1178e3d5e0d
│   │   │   ├── ebbd0433ca2c502dfee06fabec5b5e4cc4bb1d
│   │   │   └── f75381bd9362b37a9563c2abc44dd223683acf
│   │   ├── c8/
│   │   │   ├── 619ebf864f64c8a1732707bcfa7fa2faa3aeb2
│   │   │   ├── 7ef08f95a5942f85292105285e9a66b219a2ba
│   │   │   ├── c12e179cea007e10949f12a330ad1512c8eb93
│   │   │   ├── c8d03c0ab9db82021502d00225779040957d5b
│   │   │   ├── ca82294d869a5012cea2623cc166feaf8b6ed7
│   │   │   └── f155f869a5b72991a865365744c8ca64769ab7
│   │   ├── ca/
│   │   │   ├── 1ec988db8f85a5e92ad37d72d431eae1dd15b5
│   │   │   ├── 2f726e3b7957979d2b63d4a7ffde5a306ef319
│   │   │   ├── af633e81d68101c332a342c8e4bb465c7bdd82
│   │   │   └── b932c42dd7055a5431b071ebb5784629d9717f
│   │   ├── cb/
│   │   │   └── 8b225eae4150ea320b01965726a9096996e17d
│   │   ├── cc/
│   │   │   ├── 1ead347de59abe8ad7b5fba2c91f9bbdee9c97
│   │   │   ├── 7d50efbdbf428a10dfd777ad15d1770df5f04b
│   │   │   ├── 82cc6b932833c91e80dbc56c6c1b2703ec1ec7
│   │   │   ├── 947c56799598205b8fb518f3ba95843c1bab71
│   │   │   ├── 95c278643e3b8b258ece899e2b8cb0c64fc209
│   │   │   ├── cb0a7612ff3e148995c89afb1c272a45a111a2
│   │   │   └── ce9ec1a7bc26c6a08a95c5e01b291bcdfaaeed
│   │   ├── cd/
│   │   │   ├── 38837e7db5bad7d7332b38eb3d983e71fa5928
│   │   │   └── 818a34cc887ee81f764205a1d7bb4b9401b11d
│   │   ├── ce/
│   │   │   ├── 0f3d7207c51291334762bc8a7db029d13fe0dd
│   │   │   ├── cd71c2f38a3cdb37e179b65d7cb14422cb2355
│   │   │   └── e2bc1a9a9037f1b14e9d98e4cacb401630f8ab
│   │   ├── cf/
│   │   │   └── aa2c35bcf6d0c254746bf072af8d9ce3714faa
│   │   ├── d0/
│   │   │   ├── 06ad2ecca191a916db7aac594b449e67738dbe
│   │   │   ├── 34b31d11605d7249b0ceac6178c78fb327b286
│   │   │   ├── 5cf109ef231bd4387b2356816527412bc60c51
│   │   │   └── 60447bc594f3f1dbd7a3b95d61446caecb9c6b
│   │   ├── d2/
│   │   │   ├── 681b2bc48da48dab5bf2ed13b6614bd3ef4903
│   │   │   ├── 973c457997cfb876e5b246bc526dc78d8d4b60
│   │   │   ├── bf42e133ce0f15d7856b8663fa977ea7ec70b3
│   │   │   ├── c026e8845fccbe3512606d0a019b648763fb35
│   │   │   └── ec08a04d503f49193429c70b034d63f33a049e
│   │   ├── d3/
│   │   │   └── 67025798bf81e26e0131ef90f859bd4737ba50
│   │   ├── d4/
│   │   │   ├── 22261ea6d2518ee68fb1d659cd0d7caff33b3d
│   │   │   ├── 4a4b1774a4fdc036549a56140de7aa8d90218f
│   │   │   ├── 7eb1dd758aa4c3dea1cec28922cc1387dc4550
│   │   │   ├── 90afd18f72b78803b4dbb454737e3616ebe6e3
│   │   │   └── cc38db1c159ac5fe4e4866528285c73c57191b
│   │   ├── d5/
│   │   │   ├── 140f99b720cd7f81050c7f3b46512a53b798d9
│   │   │   ├── 16ad4068e4276d74e509c51b8af3e619e05b6a
│   │   │   ├── 63e3bcfd8d15c85df5be6c2eba016aa426030b
│   │   │   ├── 64d0bc3dd917926892c55e3706cc116d5b165e
│   │   │   └── abee01e02ae272adfbbdb884ffb3de7bfa97fe
│   │   ├── d6/
│   │   │   └── 83817fb24e3ecb1b792bcc0edc04fa4d067e14
│   │   ├── d7/
│   │   │   ├── 6521a7042241ce552565da79574d6dc6af1fba
│   │   │   ├── 9084d981c0ef124eb3235dbce7673344646f07
│   │   │   ├── a31d6505b2754429f8494ed065b3050f649ef9
│   │   │   └── ad1b8a0091b7bed035cc52c967afa3d2e98900
│   │   ├── d8/
│   │   │   ├── 22b7a001390a3cc010fb810cb08560c3c9d7be
│   │   │   ├── 38557215e1afb7443227133a56bbcb5370ba1a
│   │   │   ├── 50550cf022bbf9e5352910ac1a11700db1d617
│   │   │   └── 7bd805d0f1dc0b1c13da5204f3a5cd35ed1640
│   │   ├── d9/
│   │   │   └── bb07fc1223d49f78a037785118516aae2a8be4
│   │   ├── da/
│   │   │   ├── a89691f268fee864185a5e1026b2fcb82ded12
│   │   │   └── f186339ff3227dda2ece70f15189acf90ed1ce
│   │   ├── db/
│   │   │   ├── 1a3887675f3f9b2f1d44a30ca59d63387f778c
│   │   │   ├── c2621de91beab1b0d95e58dabf80bf0572546c
│   │   │   └── ff6809cc9b83aad65b3f11abe479abbe99a4ea
│   │   ├── dc/
│   │   │   ├── 298ae345889ea025632fe19f4c55632433b7b7
│   │   │   ├── a1f48b84d222e100d772a167f51e46dcd31bbc
│   │   │   └── f30c1ec034e5201fa1331d5580d9eedbca8d9f
│   │   ├── dd/
│   │   │   ├── 3e53ee4373f50b8d32396f71923bf13fd5485e
│   │   │   ├── 40aa305fe966ffd94a0f362552782c86d1936d
│   │   │   ├── 5ecceb0790aae37c1b17b6cb39dadfb5605215
│   │   │   └── c4f0c588dd7c41dedba7353e2c310d3fe52c39
│   │   ├── de/
│   │   │   ├── c737ddd6da11cbe4ae9b7d21d1250282bf2e80
│   │   │   ├── d3ad584105fe9bca60b45b5f45cec11803408e
│   │   │   └── da2018e9e39365ecc457e069ff7590095befd6
│   │   ├── df/
│   │   │   ├── 8ec2592cdf062a57c703bcd8f043311a51a765
│   │   │   ├── 9e6583f39697820881890c14e662184137eff6
│   │   │   ├── ab846ceff08c2114e3787671ef7716925b16d9
│   │   │   ├── e4432cc6d259f7d167866d511b5299301cd78d
│   │   │   └── fce98b245dddcf9156835c8eda629f806b94e4
│   │   ├── e1/
│   │   │   ├── 091afee82e38ca85f3b18710e5a52318d5b547
│   │   │   ├── 14e1ed02b472426fb09d9a2a17169f91a9294f
│   │   │   ├── 4311e981eec932b968f5280c6fcd6b8db4953d
│   │   │   ├── 55b0e96df8f978ac7e63af9ff7f7958fbd8d0b
│   │   │   └── a04ce17639c0b3d4644732cdf10039153e6fc4
│   │   ├── e2/
│   │   │   ├── 3bac0ad93a849dc108ead08f4fd26b3048fbf4
│   │   │   ├── 9538ba0e1a2b439e35661b453f1f5521bb48c7
│   │   │   ├── 99c9b195deb91391be35c1a2ed3f932d48cf6c
│   │   │   └── e85ab0c338edfbeddcb9af74dce76f96c68488
│   │   ├── e3/
│   │   │   ├── 1f4084acbbea6bf3742d84f1e6b1842c517343
│   │   │   └── 461864b62c9dc1438d145b9a5c590820a0a8cb
│   │   ├── e4/
│   │   │   ├── 4e97dff495ad4807a5dde33c3b003c2dd2485e
│   │   │   └── 58d44d0944e4f1743077631cf0121c9fd22cc5
│   │   ├── e5/
│   │   │   ├── 36ad5fdd29c24d0089dab9e860ea4a5fac38b7
│   │   │   ├── 3fdada67272c5b14fc7e8d7accaeaecc0831b9
│   │   │   └── f64d2afbef2e0fe2db6854696bb3dad6e9bd8e
│   │   ├── e6/
│   │   │   ├── 263077aeec380501d06578af3293b8be54cdf3
│   │   │   ├── 4548cd46f4a4664ba21feac4202820e1adf743
│   │   │   ├── 5dbab62f48ad87353644b9152888bc2106405b
│   │   │   ├── 9de29bb2d1d6434b8b29ae775ad8c2e48c5391
│   │   │   └── e5e8529502eac0ced71f031bd7b1ced38c0b79
│   │   ├── e7/
│   │   │   ├── 7ca383c5d1dc1d95934a5f127f5b7e74e6814a
│   │   │   └── 9e3e4209b4806bd335f1b30b6dbd769cbba30c
│   │   ├── e8/
│   │   │   ├── a1f0271166a2067e444a990b7aa4e463700a71
│   │   │   ├── b510ce62094af4af21ebcd24a3329b8b68085d
│   │   │   └── c5c75ec9ae8e0461c31a9c01896f374531f7ee
│   │   ├── e9/
│   │   │   └── e13190f9b5d4ec3af2aa49deef29305da06fa7
│   │   ├── ea/
│   │   │   ├── 24b74e8f59ce57e1987b0f23ab088c7ad4a185
│   │   │   ├── a21e8939167cff77a30e44963765d3fb704f66
│   │   │   └── f13ea38070e787d5cd3b8a0d640346a99be992
│   │   ├── eb/
│   │   │   ├── 15ee3632737085367f9a75615977abe9802f84
│   │   │   └── e74a09e5afa30157287b7686f671a36a31d735
│   │   ├── ec/
│   │   │   ├── bfd7d9948adfb1ecc1393d830a7c30e0de4ec0
│   │   │   └── f161bcb908f2636f9c3d244c8a7587d032d193
│   │   ├── ed/
│   │   │   ├── 1003672430ba53a3e1a3f24d2226f77c42d084
│   │   │   ├── b68f6bf74ab749f59e4563c2f35adcf0eb34b3
│   │   │   └── f97026b5fe82b096d3db52955390e2726a2710
│   │   ├── ee/
│   │   │   ├── 74ea35eb2029e9f4896f1eb16f03bde264cc12
│   │   │   └── 9fd8b9111fcacbcace39a8ef8770d7eefdc958
│   │   ├── ef/
│   │   │   └── 1fc1792b6c01c912602562956522b076d6e559
│   │   ├── f0/
│   │   │   ├── 35331959e307c1dd50fc29c9da8e7a94515742
│   │   │   ├── 4f7f6ebc459511431f5d1d908d4087162482c0
│   │   │   └── ed44649acb9d7ca933b3d0df2f11d7a53bd037
│   │   ├── f1/
│   │   │   ├── 021404634755144a7f410d99f75f444f34ad96
│   │   │   ├── 3717ecf00c8a1cc5f2d1b0001ba88eaaff7d72
│   │   │   └── 753e3fa3eaeead20694633c6fd75017c849641
│   │   ├── f2/
│   │   │   ├── 508f88a378ff7a3010b561a0c5683079ef4599
│   │   │   └── 93914585c3ee74e0dbe7faa8c0fab17b7856cf
│   │   ├── f3/
│   │   │   ├── 08aacc008bd433a54aa30ff5d10cdeaa183769
│   │   │   ├── 3b4e953d105b94096c31976e5e46fb75172a44
│   │   │   └── c3e438a1e341bb958068125d4f7b11867cb47b
│   │   ├── f4/
│   │   │   ├── c79097b13bef11dc78725d26f8ea8a5ca804a7
│   │   │   ├── d83ddaa08bc01dc32c7e7bba89f9eb284c0d65
│   │   │   ├── e788b3331266015c9f5e4cf7e8eca74fc2bf8b
│   │   │   ├── e86eeae53f99e09e393e1dbff358aa0d050662
│   │   │   └── e8f6572a4d71c6e355dc8be7b23f861295f528
│   │   ├── f5/
│   │   │   └── cfc8c136b4ce98ccc76c873cb614b462b2c03a
│   │   ├── f6/
│   │   │   ├── 0be82c891d8fc8af445607958628b90bdc9ddd
│   │   │   ├── 1798cfbb1b878b4e44306e0c3e82f994c3aaaa
│   │   │   ├── 5fff1fce73b4b054c260905ba118cfdea68ead
│   │   │   └── ec45a32004f92bb19eef9cfc6fa35c99254a11
│   │   ├── f7/
│   │   │   ├── 3a08ea6a9928b057c39ba5328b100c57163563
│   │   │   ├── 43501239af7b05e265778b98da95f32d076b49
│   │   │   ├── 54a5b382dfedbdbdabf50436d6565ab91999e1
│   │   │   ├── 58475b3fa161e6f7e9ffc5ac8ea3956d1b485c
│   │   │   └── c599314396227e45e2ef3e0fbbf56099866104
│   │   ├── f8/
│   │   │   ├── 63af818cca563aef3849c42f98dcd44c56c21f
│   │   │   ├── c26b179990d144590789fefa67c8bcbae6cc89
│   │   │   ├── c6127d327620c93d2b2d00342a68e97b98a48d
│   │   │   └── fda15810e657399907e099fbaa611d21dbcad0
│   │   ├── f9/
│   │   │   ├── 531c6c16dc22e57254aee2392a0478797e4dae
│   │   │   ├── 63c94d4119c15ccf00f1e92a93aeb05cec55d6
│   │   │   └── 851c11021ece98b770753532ceb4e96aa16018
│   │   ├── fb/
│   │   │   ├── 673edde33b9287336138ead67d2f9c75f8b123
│   │   │   └── 8aa90fefe374583f3916c6f9f84944593bb2a2
│   │   ├── fc/
│   │   │   └── 632dbb36aac3a39468001f2b3465a76018dbf4
│   │   ├── fe/
│   │   │   ├── 2ab95df2445337e4814f9e3c5b54c8bad8d6d8
│   │   │   ├── e26bf6a4b1471a24916d65ce983bed3599ae06
│   │   │   └── e61e61e70599a0f3a74680652e60fa8f67679d
│   │   ├── ff/
│   │   │   ├── 69a712d80432d2881866b303467591cb18bcb3
│   │   │   └── d59cea50f55f17aff55a768b28f2adb40c7932
│   │   ├── info/
│   │   └── pack/
│   │       ├── pack-eeb6dbdfd291649896254a8e1d1baea44aee6222.idx
│   │       ├── pack-eeb6dbdfd291649896254a8e1d1baea44aee6222.pack
│   │       └── pack-eeb6dbdfd291649896254a8e1d1baea44aee6222.rev
│   ├── refs/
│   │   ├── heads/
│   │   │   └── main
│   │   ├── remotes/origin/
│   │   │   ├── HEAD
│   │   │   └── main
│   │   └── tags/
│   │   ├── COMMIT_EDITMSG
│   ├── config
│   ├── description
│   ├── HEAD
│   ├── index
│   └── packed-refs
├── .gradle/
│   ├── 9.1.0/
│   │   ├── checksums/
│   │   │   ├── checksums.lock
│   │   │   ├── md5-checksums.bin
│   │   │   └── sha1-checksums.bin
│   │   ├── executionHistory/
│   │   │   ├── executionHistory.bin
│   │   │   └── executionHistory.lock
│   │   ├── expanded/
│   │   ├── fileChanges/
│   │   │   └── last-build.bin
│   │   ├── fileHashes/
│   │   │   ├── fileHashes.bin
│   │   │   ├── fileHashes.lock
│   │   │   └── resourceHashesCache.bin
│   │   ├── vcsMetadata/
│   │   │   ├── buildOutputCleanup/
│   │   ├── buildOutputCleanup.lock
│   │   │   │   └── outputFiles.bin
│   ├── kotlin/errors/
│   ├── vcs-1/
│   │   │   │   └── file-system.probe
├── .idea/
│   ├── caches/
│   │   └── deviceStreaming.xml
│   ├── inspectionProfiles/
│   │   └── Project_Default.xml
│   ├── modules/app/
│   ├── shelf/
│   │   ├── Changes/
│   │   │   └── shelved.patch
│   │   └── Changes.xml
│   ├── .gitignore
│   ├── AndroidProjectSystem.xml
│   ├── assetWizardSettings.xml
│   ├── compiler.xml
│   ├── deploymentTargetSelector.xml
│   ├── deviceManager.xml
│   ├── gradle.xml
│   ├── markdown.xml
│   ├── migrations.xml
│   ├── misc.xml
│   ├── runConfigurations.xml
│   ├── studiobot.xml
│   ├── vcs.xml
│   └── workspace.xml
├── .kotlin/
│   ├── errors/
│   │   └── errors-1772643534974.log
│   └── sessions/
├── .run/
│   └── run-folder-tree.run.xml
├── app/
│   ├── build/
│   │   ├── generated/
│   │   │   ├── ap_generated_sources/
│   │   │   │   ├── debug/out/
│   │   │   │   └── release/out/
│   │   │   ├── hilt/
│   │   │   │   ├── component_sources/
│   │   │   │   │   ├── debug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC.java
│   │   │   │   │   │   │   ├── Hilt_SafeCubeApp.java
│   │   │   │   │   │   │   ├── SafeCubeApp_GeneratedInjector.java
│   │   │   │   │   │   │   └── SafeCubeApp_HiltComponents.java
│   │   │   │   │   │   ├── dagger/hilt/internal/aggregatedroot/codegen/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.java
│   │   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.java
│   │   │   │   │   └── release/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/
│   │   │   │   │       │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC.java
│   │   │   │   │       │   ├── Hilt_SafeCubeApp.java
│   │   │   │   │       │   ├── SafeCubeApp_GeneratedInjector.java
│   │   │   │   │       │   └── SafeCubeApp_HiltComponents.java
│   │   │   │   │       ├── dagger/hilt/internal/aggregatedroot/codegen/
│   │   │   │   │       │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.java
│   │   │   │   │       └── hilt_aggregated_deps/
│   │   │   │   │           └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.java
│   │   │   │   └── component_trees/
│   │   │   │       ├── debug/
│   │   │   │       │   ├── com/miguelrodriguez19/safecube/
│   │   │   │       │   │   └── SafeCubeApp_ComponentTreeDeps.java
│   │   │   │       │   └── dagger/hilt/internal/processedrootsentinel/codegen/
│   │   │   │       │       └── _com_miguelrodriguez19_safecube_SafeCubeApp.java
│   │   │   │       └── release/
│   │   │   │           ├── com/miguelrodriguez19/safecube/
│   │   │   │           │   └── SafeCubeApp_ComponentTreeDeps.java
│   │   │   │           └── dagger/hilt/internal/processedrootsentinel/codegen/
│   │   │   │               └── _com_miguelrodriguez19_safecube_SafeCubeApp.java
│   │   │   ├── ksp/
│   │   │   │   ├── debug/java/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/
│   │   │   │   │   │   └── SafeCubeApp_GeneratedInjector.java
│   │   │   │   │   ├── dagger/hilt/internal/aggregatedroot/codegen/
│   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.java
│   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │       ├── _com_miguelrodriguez19_safecube_app_navigation_NavigationGatesEntryPoint.java
│   │   │   │   │       └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.java
│   │   │   │   └── release/java/
│   │   │   │       ├── com/miguelrodriguez19/safecube/
│   │   │   │       │   └── SafeCubeApp_GeneratedInjector.java
│   │   │   │       ├── dagger/hilt/internal/aggregatedroot/codegen/
│   │   │   │       │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.java
│   │   │   │       └── hilt_aggregated_deps/
│   │   │   │           ├── _com_miguelrodriguez19_safecube_app_navigation_NavigationGatesEntryPoint.java
│   │   │   │           └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.java
│   │   │   ├── res/pngs/
│   │   │   │   ├── debug/
│   │   │   │   └── release/
│   │   │   └── updated_navigation_xml/
│   │   │       ├── debug/
│   │   │       ├── debugAndroidTest/
│   │   │       └── release/
│   │   ├── intermediates/
│   │   │   ├── aapt_proguard_file/release/processReleaseResources/
│   │   │   │   └── aapt_rules.txt
│   │   │   ├── aar_metadata_check/
│   │   │   │   ├── debug/checkDebugAarMetadata/
│   │   │   │   ├── debugAndroidTest/checkDebugAndroidTestAarMetadata/
│   │   │   │   └── release/checkReleaseAarMetadata/
│   │   │   ├── android_res_source_set_path_map/
│   │   │   │   ├── debug/mapDebugSourceSetPaths/
│   │   │   │   │   └── file-map.txt
│   │   │   │   ├── debugAndroidTest/mapDebugAndroidTestSourceSetPaths/
│   │   │   │   │   └── file-map.txt
│   │   │   │   └── release/mapReleaseSourceSetPaths/
│   │   │   │       └── file-map.txt
│   │   │   ├── annotation_processor_list/
│   │   │   │   ├── debug/javaPreCompileDebug/
│   │   │   │   │   └── annotationProcessors.json
│   │   │   │   ├── debugAndroidTest/javaPreCompileDebugAndroidTest/
│   │   │   │   │   └── annotationProcessors.json
│   │   │   │   ├── debugUnitTest/javaPreCompileDebugUnitTest/
│   │   │   │   │   └── annotationProcessors.json
│   │   │   │   └── release/javaPreCompileRelease/
│   │   │   │       └── annotationProcessors.json
│   │   │   ├── apk/
│   │   │   │   ├── debug/
│   │   │   │   │   ├── app-debug.apk
│   │   │   │   │   └── output-metadata.json
│   │   │   │   └── release/
│   │   │   │       ├── baselineProfiles/
│   │   │   │       │   ├── 0/
│   │   │   │       │   │   └── app-release.dm
│   │   │   │       │   └── 1/
│   │   │   │       │       └── app-release.dm
│   │   │   │       ├── app-release.apk
│   │   │   │       └── output-metadata.json
│   │   │   ├── apk_for_local_test/debugUnitTest/packageDebugUnitTestForUnitTest/
│   │   │   │   └── apk-for-local-test.ap_
│   │   │   ├── apk_ide_redirect_file/
│   │   │   │   ├── debug/createDebugApkListingFileRedirect/
│   │   │   │   │   └── redirect.txt
│   │   │   │   ├── debugAndroidTest/createDebugAndroidTestApkListingFileRedirect/
│   │   │   │   │   └── redirect.txt
│   │   │   │   └── release/createReleaseApkListingFileRedirect/
│   │   │   │       └── redirect.txt
│   │   │   ├── app_metadata/
│   │   │   │   ├── debug/writeDebugAppMetadata/
│   │   │   │   │   │   │   │   │   └── release/writeReleaseAppMetadata/
│   │   │   │       │   │   │   ├── assets/
│   │   │   │   ├── debug/mergeDebugAssets/
│   │   │   │   │   └── PublicSuffixDatabase.list
│   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestAssets/
│   │   │   │   └── release/mergeReleaseAssets/
│   │   │   │       └── PublicSuffixDatabase.list
│   │   │   ├── binary_art_profile/release/compileReleaseArtProfile/
│   │   │   │   └── baseline.prof
│   │   │   ├── binary_art_profile_metadata/release/compileReleaseArtProfile/
│   │   │   │   └── baseline.profm
│   │   │   ├── built_in_kotlinc/
│   │   │   │   ├── debug/compileDebugKotlin/classes/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/
│   │   │   │   │   │   ├── app/navigation/
│   │   │   │   │   │   │   ├── ComposableSingletons$NavigationWrapperKt.class
│   │   │   │   │   │   │   ├── NavigationGatesEntryPoint.class
│   │   │   │   │   │   │   ├── NavigationGatesKt$PostLoginGateRoute$1$1.class
│   │   │   │   │   │   │   ├── NavigationGatesKt$SplashGateScreen$1$1.class
│   │   │   │   │   │   │   ├── NavigationGatesKt.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$$inlined$entryProvider$default$1.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$1.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$10.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$11.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$12.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$13.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$2.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$3.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$4.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$5.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$6.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$7.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$8.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$9.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt.class
│   │   │   │   │   │   │   ├── Routes$App.class
│   │   │   │   │   │   │   ├── Routes$CreateVault.class
│   │   │   │   │   │   │   ├── Routes$Error.class
│   │   │   │   │   │   │   ├── Routes$Login.class
│   │   │   │   │   │   │   ├── Routes$PostLoginGate.class
│   │   │   │   │   │   │   ├── Routes$Profile.class
│   │   │   │   │   │   │   ├── Routes$RecoveryKey.class
│   │   │   │   │   │   │   ├── Routes$Settings.class
│   │   │   │   │   │   │   ├── Routes$Signup.class
│   │   │   │   │   │   │   ├── Routes$Splash.class
│   │   │   │   │   │   │   ├── Routes$UnlockVault.class
│   │   │   │   │   │   │   ├── Routes$Vault.class
│   │   │   │   │   │   │   ├── Routes$VaultFolders.class
│   │   │   │   │   │   │   ├── Routes$Welcome.class
│   │   │   │   │   │   │   ├── Routes.class
│   │   │   │   │   │   │   └── RoutesKt.class
│   │   │   │   │   │   ├── ui/theme/
│   │   │   │   │   │   │   ├── ColorKt.class
│   │   │   │   │   │   │   ├── ThemeKt.class
│   │   │   │   │   │   │   └── TypeKt.class
│   │   │   │   │   │   ├── ComposableSingletons$MainActivityKt.class
│   │   │   │   │   │   ├── MainActivity.class
│   │   │   │   │   │   └── SafeCubeApp.class
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── app.kotlin_module
│   │   │   │   ├── debugAndroidTest/compileDebugAndroidTestKotlin/classes/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/
│   │   │   │   │   │   ├── ComposableSingletons$MainActivityComposeTestKt.class
│   │   │   │   │   │   ├── ExampleInstrumentedTest.class
│   │   │   │   │   │   └── MainActivityComposeTest.class
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── app.kotlin_module
│   │   │   │   ├── debugUnitTest/compileDebugUnitTestKotlin/classes/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/
│   │   │   │   │   │   └── TestingSetupUnitTest.class
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── app.kotlin_module
│   │   │   │   └── release/compileReleaseKotlin/classes/
│   │   │   │       ├── com/miguelrodriguez19/safecube/
│   │   │   │       │   ├── app/navigation/
│   │   │   │       │   │   ├── ComposableSingletons$NavigationWrapperKt.class
│   │   │   │       │   │   ├── NavigationGatesEntryPoint.class
│   │   │   │       │   │   ├── NavigationGatesKt$PostLoginGateRoute$1$1.class
│   │   │   │       │   │   ├── NavigationGatesKt$SplashGateScreen$1$1.class
│   │   │   │       │   │   ├── NavigationGatesKt.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$$inlined$entryProvider$default$1.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$1.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$10.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$11.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$12.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$13.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$2.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$3.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$4.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$5.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$6.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$7.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$8.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$9.class
│   │   │   │       │   │   ├── NavigationWrapperKt.class
│   │   │   │       │   │   ├── Routes$App.class
│   │   │   │       │   │   ├── Routes$CreateVault.class
│   │   │   │       │   │   ├── Routes$Error.class
│   │   │   │       │   │   ├── Routes$Login.class
│   │   │   │       │   │   ├── Routes$PostLoginGate.class
│   │   │   │       │   │   ├── Routes$Profile.class
│   │   │   │       │   │   ├── Routes$RecoveryKey.class
│   │   │   │       │   │   ├── Routes$Settings.class
│   │   │   │       │   │   ├── Routes$Signup.class
│   │   │   │       │   │   ├── Routes$Splash.class
│   │   │   │       │   │   ├── Routes$UnlockVault.class
│   │   │   │       │   │   ├── Routes$Vault.class
│   │   │   │       │   │   ├── Routes$VaultFolders.class
│   │   │   │       │   │   ├── Routes$Welcome.class
│   │   │   │       │   │   ├── Routes.class
│   │   │   │       │   │   └── RoutesKt.class
│   │   │   │       │   ├── ui/theme/
│   │   │   │       │   │   ├── ColorKt.class
│   │   │   │       │   │   ├── ThemeKt.class
│   │   │   │       │   │   └── TypeKt.class
│   │   │   │       │   ├── ComposableSingletons$MainActivityKt.class
│   │   │   │       │   ├── MainActivity.class
│   │   │   │       │   └── SafeCubeApp.class
│   │   │   │       └── META-INF/
│   │   │   │           └── app.kotlin_module
│   │   │   ├── classes/
│   │   │   │   ├── debug/
│   │   │   │   │   ├── hiltJavaCompileDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityCBuilder.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityCImpl.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCBuilder.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCImpl$SwitchingProvider.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCImpl.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$Builder.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$FragmentCBuilder.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$FragmentCImpl.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ServiceCBuilder.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ServiceCImpl.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$SingletonCImpl$SwitchingProvider.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$SingletonCImpl.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewCBuilder.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewCImpl.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewModelCBuilder.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewModelCImpl.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewWithFragmentCBuilder.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewWithFragmentCImpl.class
│   │   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC.class
│   │   │   │   │   │   │   ├── Hilt_SafeCubeApp$1.class
│   │   │   │   │   │   │   ├── Hilt_SafeCubeApp.class
│   │   │   │   │   │   │   ├── SafeCubeApp_ComponentTreeDeps.class
│   │   │   │   │   │   │   ├── SafeCubeApp_GeneratedInjector.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ActivityC$Builder.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ActivityC.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ActivityCBuilderModule.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedC$Builder.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedC.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedCBuilderModule.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$FragmentC$Builder.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$FragmentC.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$FragmentCBuilderModule.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ServiceC$Builder.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ServiceC.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ServiceCBuilderModule.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$SingletonC.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewC$Builder.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewC.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewCBuilderModule.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewModelC$Builder.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewModelC.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewModelCBuilderModule.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentC$Builder.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentC.class
│   │   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentCBuilderModule.class
│   │   │   │   │   │   │   └── SafeCubeApp_HiltComponents.class
│   │   │   │   │   │   ├── dagger/hilt/internal/
│   │   │   │   │   │   │   ├── aggregatedroot/codegen/
│   │   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.class
│   │   │   │   │   │   │   └── processedrootsentinel/codegen/
│   │   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_SafeCubeApp.class
│   │   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.class
│   │   │   │   │   └── transformDebugClassesWithAsm/
│   │   │   │   │       ├── dirs/
│   │   │   │   │       │   ├── com/miguelrodriguez19/safecube/
│   │   │   │   │       │   │   ├── app/navigation/
│   │   │   │   │       │   │   │   ├── ComposableSingletons$NavigationWrapperKt.class
│   │   │   │   │       │   │   │   ├── NavigationGatesEntryPoint.class
│   │   │   │   │       │   │   │   ├── NavigationGatesKt$PostLoginGateRoute$1$1.class
│   │   │   │   │       │   │   │   ├── NavigationGatesKt$SplashGateScreen$1$1.class
│   │   │   │   │       │   │   │   ├── NavigationGatesKt.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt$NavigationWrapper$$inlined$entryProvider$default$1.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$1.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$10.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$11.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$12.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$13.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$2.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$3.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$4.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$5.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$6.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$7.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$8.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$9.class
│   │   │   │   │       │   │   │   ├── NavigationWrapperKt.class
│   │   │   │   │       │   │   │   ├── Routes$App.class
│   │   │   │   │       │   │   │   ├── Routes$CreateVault.class
│   │   │   │   │       │   │   │   ├── Routes$Error.class
│   │   │   │   │       │   │   │   ├── Routes$Login.class
│   │   │   │   │       │   │   │   ├── Routes$PostLoginGate.class
│   │   │   │   │       │   │   │   ├── Routes$Profile.class
│   │   │   │   │       │   │   │   ├── Routes$RecoveryKey.class
│   │   │   │   │       │   │   │   ├── Routes$Settings.class
│   │   │   │   │       │   │   │   ├── Routes$Signup.class
│   │   │   │   │       │   │   │   ├── Routes$Splash.class
│   │   │   │   │       │   │   │   ├── Routes$UnlockVault.class
│   │   │   │   │       │   │   │   ├── Routes$Vault.class
│   │   │   │   │       │   │   │   ├── Routes$VaultFolders.class
│   │   │   │   │       │   │   │   ├── Routes$Welcome.class
│   │   │   │   │       │   │   │   ├── Routes.class
│   │   │   │   │       │   │   │   └── RoutesKt.class
│   │   │   │   │       │   │   ├── ui/theme/
│   │   │   │   │       │   │   │   ├── ColorKt.class
│   │   │   │   │       │   │   │   ├── ThemeKt.class
│   │   │   │   │       │   │   │   └── TypeKt.class
│   │   │   │   │       │   │   ├── ComposableSingletons$MainActivityKt.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityCBuilder.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityCImpl.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCBuilder.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCImpl$SwitchingProvider.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCImpl.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$Builder.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$FragmentCBuilder.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$FragmentCImpl.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ServiceCBuilder.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ServiceCImpl.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$SingletonCImpl$SwitchingProvider.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$SingletonCImpl.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewCBuilder.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewCImpl.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewModelCBuilder.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewModelCImpl.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewWithFragmentCBuilder.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewWithFragmentCImpl.class
│   │   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC.class
│   │   │   │   │       │   │   ├── Hilt_SafeCubeApp$1.class
│   │   │   │   │       │   │   ├── Hilt_SafeCubeApp.class
│   │   │   │   │       │   │   ├── MainActivity.class
│   │   │   │   │       │   │   ├── SafeCubeApp.class
│   │   │   │   │       │   │   ├── SafeCubeApp_ComponentTreeDeps.class
│   │   │   │   │       │   │   ├── SafeCubeApp_GeneratedInjector.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ActivityC$Builder.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ActivityC.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ActivityCBuilderModule.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedC$Builder.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedC.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedCBuilderModule.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$FragmentC$Builder.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$FragmentC.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$FragmentCBuilderModule.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ServiceC$Builder.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ServiceC.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ServiceCBuilderModule.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$SingletonC.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewC$Builder.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewC.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewCBuilderModule.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewModelC$Builder.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewModelC.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewModelCBuilderModule.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentC$Builder.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentC.class
│   │   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentCBuilderModule.class
│   │   │   │   │       │   │   └── SafeCubeApp_HiltComponents.class
│   │   │   │   │       │   ├── dagger/hilt/internal/
│   │   │   │   │       │   │   ├── aggregatedroot/codegen/
│   │   │   │   │       │   │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.class
│   │   │   │   │       │   │   └── processedrootsentinel/codegen/
│   │   │   │   │       │   │       └── _com_miguelrodriguez19_safecube_SafeCubeApp.class
│   │   │   │   │       │   ├── hilt_aggregated_deps/
│   │   │   │   │       │   │   ├── _com_miguelrodriguez19_safecube_app_navigation_NavigationGatesEntryPoint.class
│   │   │   │   │       │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.class
│   │   │   │   │       │   └── META-INF/
│   │   │   │   │       │       └── app.kotlin_module
│   │   │   │   │       └── jars/
│   │   │   │   │           └── 0.jar
│   │   │   │   └── release/
│   │   │   │       ├── hiltJavaCompileRelease/
│   │   │   │       │   ├── com/miguelrodriguez19/safecube/
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityCBuilder.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityCImpl.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCBuilder.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCImpl$SwitchingProvider.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCImpl.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$Builder.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$FragmentCBuilder.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$FragmentCImpl.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ServiceCBuilder.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ServiceCImpl.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$SingletonCImpl$SwitchingProvider.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$SingletonCImpl.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewCBuilder.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewCImpl.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewModelCBuilder.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewModelCImpl.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewWithFragmentCBuilder.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewWithFragmentCImpl.class
│   │   │   │       │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC.class
│   │   │   │       │   │   ├── Hilt_SafeCubeApp$1.class
│   │   │   │       │   │   ├── Hilt_SafeCubeApp.class
│   │   │   │       │   │   ├── SafeCubeApp_ComponentTreeDeps.class
│   │   │   │       │   │   ├── SafeCubeApp_GeneratedInjector.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ActivityC$Builder.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ActivityC.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ActivityCBuilderModule.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedC$Builder.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedC.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedCBuilderModule.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$FragmentC$Builder.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$FragmentC.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$FragmentCBuilderModule.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ServiceC$Builder.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ServiceC.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ServiceCBuilderModule.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$SingletonC.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewC$Builder.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewC.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewCBuilderModule.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewModelC$Builder.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewModelC.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewModelCBuilderModule.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentC$Builder.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentC.class
│   │   │   │       │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentCBuilderModule.class
│   │   │   │       │   │   └── SafeCubeApp_HiltComponents.class
│   │   │   │       │   ├── dagger/hilt/internal/
│   │   │   │       │   │   ├── aggregatedroot/codegen/
│   │   │   │       │   │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.class
│   │   │   │       │   │   └── processedrootsentinel/codegen/
│   │   │   │       │   │       └── _com_miguelrodriguez19_safecube_SafeCubeApp.class
│   │   │   │       │   └── hilt_aggregated_deps/
│   │   │   │       │       └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.class
│   │   │   │       └── transformReleaseClassesWithAsm/
│   │   │   │           ├── dirs/
│   │   │   │           │   ├── com/miguelrodriguez19/safecube/
│   │   │   │           │   │   ├── app/navigation/
│   │   │   │           │   │   │   ├── ComposableSingletons$NavigationWrapperKt.class
│   │   │   │           │   │   │   ├── NavigationGatesEntryPoint.class
│   │   │   │           │   │   │   ├── NavigationGatesKt$PostLoginGateRoute$1$1.class
│   │   │   │           │   │   │   ├── NavigationGatesKt$SplashGateScreen$1$1.class
│   │   │   │           │   │   │   ├── NavigationGatesKt.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt$NavigationWrapper$$inlined$entryProvider$default$1.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$1.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$10.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$11.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$12.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$13.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$2.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$3.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$4.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$5.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$6.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$7.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$8.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$9.class
│   │   │   │           │   │   │   ├── NavigationWrapperKt.class
│   │   │   │           │   │   │   ├── Routes$App.class
│   │   │   │           │   │   │   ├── Routes$CreateVault.class
│   │   │   │           │   │   │   ├── Routes$Error.class
│   │   │   │           │   │   │   ├── Routes$Login.class
│   │   │   │           │   │   │   ├── Routes$PostLoginGate.class
│   │   │   │           │   │   │   ├── Routes$Profile.class
│   │   │   │           │   │   │   ├── Routes$RecoveryKey.class
│   │   │   │           │   │   │   ├── Routes$Settings.class
│   │   │   │           │   │   │   ├── Routes$Signup.class
│   │   │   │           │   │   │   ├── Routes$Splash.class
│   │   │   │           │   │   │   ├── Routes$UnlockVault.class
│   │   │   │           │   │   │   ├── Routes$Vault.class
│   │   │   │           │   │   │   ├── Routes$VaultFolders.class
│   │   │   │           │   │   │   ├── Routes$Welcome.class
│   │   │   │           │   │   │   ├── Routes.class
│   │   │   │           │   │   │   └── RoutesKt.class
│   │   │   │           │   │   ├── ui/theme/
│   │   │   │           │   │   │   ├── ColorKt.class
│   │   │   │           │   │   │   ├── ThemeKt.class
│   │   │   │           │   │   │   └── TypeKt.class
│   │   │   │           │   │   ├── ComposableSingletons$MainActivityKt.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityCBuilder.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityCImpl.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCBuilder.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCImpl$SwitchingProvider.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCImpl.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$Builder.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$FragmentCBuilder.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$FragmentCImpl.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ServiceCBuilder.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ServiceCImpl.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$SingletonCImpl$SwitchingProvider.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$SingletonCImpl.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewCBuilder.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewCImpl.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewModelCBuilder.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewModelCImpl.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewWithFragmentCBuilder.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewWithFragmentCImpl.class
│   │   │   │           │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC.class
│   │   │   │           │   │   ├── Hilt_SafeCubeApp$1.class
│   │   │   │           │   │   ├── Hilt_SafeCubeApp.class
│   │   │   │           │   │   ├── MainActivity.class
│   │   │   │           │   │   ├── SafeCubeApp.class
│   │   │   │           │   │   ├── SafeCubeApp_ComponentTreeDeps.class
│   │   │   │           │   │   ├── SafeCubeApp_GeneratedInjector.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ActivityC$Builder.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ActivityC.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ActivityCBuilderModule.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedC$Builder.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedC.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedCBuilderModule.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$FragmentC$Builder.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$FragmentC.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$FragmentCBuilderModule.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ServiceC$Builder.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ServiceC.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ServiceCBuilderModule.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$SingletonC.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ViewC$Builder.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ViewC.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ViewCBuilderModule.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ViewModelC$Builder.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ViewModelC.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ViewModelCBuilderModule.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentC$Builder.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentC.class
│   │   │   │           │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentCBuilderModule.class
│   │   │   │           │   │   └── SafeCubeApp_HiltComponents.class
│   │   │   │           │   ├── dagger/hilt/internal/
│   │   │   │           │   │   ├── aggregatedroot/codegen/
│   │   │   │           │   │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.class
│   │   │   │           │   │   └── processedrootsentinel/codegen/
│   │   │   │           │   │       └── _com_miguelrodriguez19_safecube_SafeCubeApp.class
│   │   │   │           │   ├── hilt_aggregated_deps/
│   │   │   │           │   │   ├── _com_miguelrodriguez19_safecube_app_navigation_NavigationGatesEntryPoint.class
│   │   │   │           │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.class
│   │   │   │           │   └── META-INF/
│   │   │   │           │       └── app.kotlin_module
│   │   │   │           └── jars/
│   │   │   │               └── 0.jar
│   │   │   ├── combined_art_profile/release/compileReleaseArtProfile/
│   │   │   │   └── baseline-prof.txt
│   │   │   ├── compatible_screen_manifest/
│   │   │   │   ├── debug/createDebugCompatibleScreenManifests/
│   │   │   │   │   └── output-metadata.json
│   │   │   │   └── release/createReleaseCompatibleScreenManifests/
│   │   │   │       └── output-metadata.json
│   │   │   ├── compile_and_runtime_r_class_jar/
│   │   │   │   ├── debug/processDebugResources/
│   │   │   │   │   └── R.jar
│   │   │   │   ├── debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── R.jar
│   │   │   │   └── release/processReleaseResources/
│   │   │   │       └── R.jar
│   │   │   ├── compile_app_classes_jar/debug/bundleDebugClassesToCompileJar/
│   │   │   │   └── classes.jar
│   │   │   ├── compile_r_class_jar/
│   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   └── R.jar
│   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   └── R.jar
│   │   │   │   └── release/generateReleaseRFile/
│   │   │   │       └── R.jar
│   │   │   ├── compile_symbol_list/
│   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   └── R.txt
│   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   └── R.txt
│   │   │   │   └── release/generateReleaseRFile/
│   │   │   │       └── R.txt
│   │   │   ├── compiled_navigation_res/
│   │   │   │   ├── debug/compileDebugNavigationResources/
│   │   │   │   ├── debugAndroidTest/compileDebugAndroidTestNavigationResources/
│   │   │   │   └── release/compileReleaseNavigationResources/
│   │   │   ├── compose_mapping/release/
│   │   │   │   ├── compose-mapping-errors.txt
│   │   │   │   └── compose-mapping.txt
│   │   │   ├── compressed_assets/
│   │   │   │   ├── debug/compressDebugAssets/out/assets/
│   │   │   │   │   └── PublicSuffixDatabase.list.jar
│   │   │   │   ├── debugAndroidTest/compressDebugAndroidTestAssets/out/
│   │   │   │   └── release/compressReleaseAssets/out/assets/
│   │   │   │       └── PublicSuffixDatabase.list.jar
│   │   │   ├── data_binding_layout_info_type_merge/
│   │   │   │   ├── debug/mergeDebugResources/out/
│   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestResources/out/
│   │   │   │   └── release/mergeReleaseResources/out/
│   │   │   ├── data_binding_layout_info_type_package/
│   │   │   │   ├── debug/packageDebugResources/out/
│   │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/out/
│   │   │   │   └── release/packageReleaseResources/out/
│   │   │   ├── default_proguard_files/global/
│   │   │   │   ├── proguard-android-optimize.txt-9.0.1
│   │   │   │   └── proguard-android.txt-9.0.1
│   │   │   ├── desugar_graph/
│   │   │   │   ├── debug/dexBuilderDebug/out/
│   │   │   │   │   ├── currentProject/
│   │   │   │   │   │   ├── dirs_bucket_0/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── dirs_bucket_1/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── dirs_bucket_2/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── dirs_bucket_3/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── dirs_bucket_4/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── dirs_bucket_5/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_09496966a94557e218b1b40ff8dd261d9cfaff79319b08ecc112512b9a995723_bucket_0/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_09496966a94557e218b1b40ff8dd261d9cfaff79319b08ecc112512b9a995723_bucket_1/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_09496966a94557e218b1b40ff8dd261d9cfaff79319b08ecc112512b9a995723_bucket_2/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_09496966a94557e218b1b40ff8dd261d9cfaff79319b08ecc112512b9a995723_bucket_3/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_09496966a94557e218b1b40ff8dd261d9cfaff79319b08ecc112512b9a995723_bucket_4/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   └── jar_09496966a94557e218b1b40ff8dd261d9cfaff79319b08ecc112512b9a995723_bucket_5/
│   │   │   │   │   │       └── graph.bin
│   │   │   │   │   ├── externalLibs/
│   │   │   │   │   ├── mixedScopes/
│   │   │   │   │   └── otherProjects/
│   │   │   │   └── debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │       ├── currentProject/
│   │   │   │       │   ├── dirs_bucket_0/
│   │   │   │       │   │   └── graph.bin
│   │   │   │       │   ├── dirs_bucket_1/
│   │   │   │       │   │   └── graph.bin
│   │   │   │       │   ├── dirs_bucket_2/
│   │   │   │       │   │   └── graph.bin
│   │   │   │       │   ├── dirs_bucket_3/
│   │   │   │       │   │   └── graph.bin
│   │   │   │       │   ├── dirs_bucket_4/
│   │   │   │       │   │   └── graph.bin
│   │   │   │       │   ├── dirs_bucket_5/
│   │   │   │       │   │   └── graph.bin
│   │   │   │       │   ├── jar_7da5ceb42d17edeb3380d6086d0a8888a6db142b1ba9086c65f2c29c1ca7f624_bucket_0/
│   │   │   │       │   │   └── graph.bin
│   │   │   │       │   ├── jar_7da5ceb42d17edeb3380d6086d0a8888a6db142b1ba9086c65f2c29c1ca7f624_bucket_1/
│   │   │   │       │   │   └── graph.bin
│   │   │   │       │   ├── jar_7da5ceb42d17edeb3380d6086d0a8888a6db142b1ba9086c65f2c29c1ca7f624_bucket_2/
│   │   │   │       │   │   └── graph.bin
│   │   │   │       │   ├── jar_7da5ceb42d17edeb3380d6086d0a8888a6db142b1ba9086c65f2c29c1ca7f624_bucket_3/
│   │   │   │       │   │   └── graph.bin
│   │   │   │       │   ├── jar_7da5ceb42d17edeb3380d6086d0a8888a6db142b1ba9086c65f2c29c1ca7f624_bucket_4/
│   │   │   │       │   │   └── graph.bin
│   │   │   │       │   └── jar_7da5ceb42d17edeb3380d6086d0a8888a6db142b1ba9086c65f2c29c1ca7f624_bucket_5/
│   │   │   │       │       └── graph.bin
│   │   │   │       ├── externalLibs/
│   │   │   │       ├── mixedScopes/
│   │   │   │       └── otherProjects/
│   │   │   ├── dex/
│   │   │   │   ├── debug/
│   │   │   │   │   ├── mergeExtDexDebug/
│   │   │   │   │   │   ├── classes.dex
│   │   │   │   │   │   ├── classes2.dex
│   │   │   │   │   │   └── classes3.dex
│   │   │   │   │   ├── mergeLibDexDebug/
│   │   │   │   │   │   ├── 0/
│   │   │   │   │   │   ├── 1/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 10/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 11/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 12/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 13/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 14/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 15/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 2/
│   │   │   │   │   │   ├── 3/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 4/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 5/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 6/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 7/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 8/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   └── 9/
│   │   │   │   │   └── mergeProjectDexDebug/
│   │   │   │   │       ├── 0/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 1/
│   │   │   │   │       ├── 10/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 11/
│   │   │   │   │       ├── 12/
│   │   │   │   │       ├── 13/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 14/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 15/
│   │   │   │   │       ├── 2/
│   │   │   │   │       ├── 3/
│   │   │   │   │       ├── 4/
│   │   │   │   │       ├── 5/
│   │   │   │   │       ├── 6/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 7/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 8/
│   │   │   │   │       └── 9/
│   │   │   │   ├── debugAndroidTest/
│   │   │   │   │   ├── mergeExtDexDebugAndroidTest/
│   │   │   │   │   │   └── classes.dex
│   │   │   │   │   ├── mergeLibDexDebugAndroidTest/
│   │   │   │   │   │   ├── 0/
│   │   │   │   │   │   ├── 1/
│   │   │   │   │   │   ├── 10/
│   │   │   │   │   │   ├── 11/
│   │   │   │   │   │   ├── 12/
│   │   │   │   │   │   ├── 13/
│   │   │   │   │   │   ├── 14/
│   │   │   │   │   │   ├── 15/
│   │   │   │   │   │   ├── 2/
│   │   │   │   │   │   ├── 3/
│   │   │   │   │   │   ├── 4/
│   │   │   │   │   │   ├── 5/
│   │   │   │   │   │   ├── 6/
│   │   │   │   │   │   ├── 7/
│   │   │   │   │   │   ├── 8/
│   │   │   │   │   │   └── 9/
│   │   │   │   │   └── mergeProjectDexDebugAndroidTest/
│   │   │   │   │       ├── 0/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 1/
│   │   │   │   │       ├── 10/
│   │   │   │   │       ├── 11/
│   │   │   │   │       ├── 12/
│   │   │   │   │       ├── 13/
│   │   │   │   │       ├── 14/
│   │   │   │   │       ├── 15/
│   │   │   │   │       ├── 2/
│   │   │   │   │       ├── 3/
│   │   │   │   │       ├── 4/
│   │   │   │   │       ├── 5/
│   │   │   │   │       ├── 6/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 7/
│   │   │   │   │       ├── 8/
│   │   │   │   │       └── 9/
│   │   │   │   └── release/minifyReleaseWithR8/
│   │   │   │       └── classes.dex
│   │   │   ├── dex_archive_input_jar_hashes/
│   │   │   │   ├── debug/dexBuilderDebug/
│   │   │   │   │   └── out
│   │   │   │   └── debugAndroidTest/dexBuilderDebugAndroidTest/
│   │   │   │       └── out
│   │   │   ├── dex_metadata_directory/release/compileReleaseArtProfile/
│   │   │   │   ├── 0/
│   │   │   │   │   └── .dm
│   │   │   │   ├── 1/
│   │   │   │   │   └── .dm
│   │   │   │   │   │   │   ├── dex_number_of_buckets_file/
│   │   │   │   ├── debug/dexBuilderDebug/
│   │   │   │   │   └── out
│   │   │   │   └── debugAndroidTest/dexBuilderDebugAndroidTest/
│   │   │   │       └── out
│   │   │   ├── duplicate_classes_check/
│   │   │   │   ├── debug/checkDebugDuplicateClasses/
│   │   │   │   ├── debugAndroidTest/checkDebugAndroidTestDuplicateClasses/
│   │   │   │   └── release/checkReleaseDuplicateClasses/
│   │   │   ├── external_file_lib_dex_archives/
│   │   │   │   ├── debug/desugarDebugFileDependencies/
│   │   │   │   └── debugAndroidTest/desugarDebugAndroidTestFileDependencies/
│   │   │   ├── external_libs_dex_archive/
│   │   │   │   ├── debug/dexBuilderDebug/out/
│   │   │   │   └── debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   ├── external_libs_dex_archive_with_artifact_transforms/
│   │   │   │   ├── debug/dexBuilderDebug/out/
│   │   │   │   └── debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   ├── generated_proguard_file/release/mergeReleaseGeneratedProguardFiles/
│   │   │   ├── hilt/copy/
│   │   │   │   ├── debug/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/
│   │   │   │   │   │   ├── app/navigation/
│   │   │   │   │   │   │   ├── ComposableSingletons$NavigationWrapperKt.class
│   │   │   │   │   │   │   ├── NavigationGatesEntryPoint.class
│   │   │   │   │   │   │   ├── NavigationGatesKt$PostLoginGateRoute$1$1.class
│   │   │   │   │   │   │   ├── NavigationGatesKt$SplashGateScreen$1$1.class
│   │   │   │   │   │   │   ├── NavigationGatesKt.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$$inlined$entryProvider$default$1.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$1.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$10.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$11.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$12.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$13.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$2.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$3.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$4.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$5.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$6.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$7.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$8.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$9.class
│   │   │   │   │   │   │   ├── NavigationWrapperKt.class
│   │   │   │   │   │   │   ├── Routes$App.class
│   │   │   │   │   │   │   ├── Routes$CreateVault.class
│   │   │   │   │   │   │   ├── Routes$Error.class
│   │   │   │   │   │   │   ├── Routes$Login.class
│   │   │   │   │   │   │   ├── Routes$PostLoginGate.class
│   │   │   │   │   │   │   ├── Routes$Profile.class
│   │   │   │   │   │   │   ├── Routes$RecoveryKey.class
│   │   │   │   │   │   │   ├── Routes$Settings.class
│   │   │   │   │   │   │   ├── Routes$Signup.class
│   │   │   │   │   │   │   ├── Routes$Splash.class
│   │   │   │   │   │   │   ├── Routes$UnlockVault.class
│   │   │   │   │   │   │   ├── Routes$Vault.class
│   │   │   │   │   │   │   ├── Routes$VaultFolders.class
│   │   │   │   │   │   │   ├── Routes$Welcome.class
│   │   │   │   │   │   │   ├── Routes.class
│   │   │   │   │   │   │   └── RoutesKt.class
│   │   │   │   │   │   ├── ui/theme/
│   │   │   │   │   │   │   ├── ColorKt.class
│   │   │   │   │   │   │   ├── ThemeKt.class
│   │   │   │   │   │   │   └── TypeKt.class
│   │   │   │   │   │   ├── ComposableSingletons$MainActivityKt.class
│   │   │   │   │   │   ├── MainActivity.class
│   │   │   │   │   │   ├── SafeCubeApp.class
│   │   │   │   │   │   └── SafeCubeApp_GeneratedInjector.class
│   │   │   │   │   ├── dagger/hilt/internal/aggregatedroot/codegen/
│   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.class
│   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   ├── _com_miguelrodriguez19_safecube_app_navigation_NavigationGatesEntryPoint.class
│   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.class
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── app.kotlin_module
│   │   │   │   └── release/
│   │   │   │       ├── com/miguelrodriguez19/safecube/
│   │   │   │       │   ├── app/navigation/
│   │   │   │       │   │   ├── ComposableSingletons$NavigationWrapperKt.class
│   │   │   │       │   │   ├── NavigationGatesEntryPoint.class
│   │   │   │       │   │   ├── NavigationGatesKt$PostLoginGateRoute$1$1.class
│   │   │   │       │   │   ├── NavigationGatesKt$SplashGateScreen$1$1.class
│   │   │   │       │   │   ├── NavigationGatesKt.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$$inlined$entryProvider$default$1.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$1.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$10.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$11.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$12.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$13.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$2.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$3.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$4.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$5.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$6.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$7.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$8.class
│   │   │   │       │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$9.class
│   │   │   │       │   │   ├── NavigationWrapperKt.class
│   │   │   │       │   │   ├── Routes$App.class
│   │   │   │       │   │   ├── Routes$CreateVault.class
│   │   │   │       │   │   ├── Routes$Error.class
│   │   │   │       │   │   ├── Routes$Login.class
│   │   │   │       │   │   ├── Routes$PostLoginGate.class
│   │   │   │       │   │   ├── Routes$Profile.class
│   │   │   │       │   │   ├── Routes$RecoveryKey.class
│   │   │   │       │   │   ├── Routes$Settings.class
│   │   │   │       │   │   ├── Routes$Signup.class
│   │   │   │       │   │   ├── Routes$Splash.class
│   │   │   │       │   │   ├── Routes$UnlockVault.class
│   │   │   │       │   │   ├── Routes$Vault.class
│   │   │   │       │   │   ├── Routes$VaultFolders.class
│   │   │   │       │   │   ├── Routes$Welcome.class
│   │   │   │       │   │   ├── Routes.class
│   │   │   │       │   │   └── RoutesKt.class
│   │   │   │       │   ├── ui/theme/
│   │   │   │       │   │   ├── ColorKt.class
│   │   │   │       │   │   ├── ThemeKt.class
│   │   │   │       │   │   └── TypeKt.class
│   │   │   │       │   ├── ComposableSingletons$MainActivityKt.class
│   │   │   │       │   ├── MainActivity.class
│   │   │   │       │   ├── SafeCubeApp.class
│   │   │   │       │   └── SafeCubeApp_GeneratedInjector.class
│   │   │   │       ├── dagger/hilt/internal/aggregatedroot/codegen/
│   │   │   │       │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.class
│   │   │   │       ├── hilt_aggregated_deps/
│   │   │   │       │   ├── _com_miguelrodriguez19_safecube_app_navigation_NavigationGatesEntryPoint.class
│   │   │   │       │   └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.class
│   │   │   │       └── META-INF/
│   │   │   │           └── app.kotlin_module
│   │   │   ├── incremental/
│   │   │   │   ├── debug/
│   │   │   │   │   ├── mergeDebugResources/
│   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   │   ├── values/
│   │   │   │   │   │   │   │   └── values.xml
│   │   │   │   │   │   │   ├── values-af/
│   │   │   │   │   │   │   │   └── values-af.xml
│   │   │   │   │   │   │   ├── values-am/
│   │   │   │   │   │   │   │   └── values-am.xml
│   │   │   │   │   │   │   ├── values-ar/
│   │   │   │   │   │   │   │   └── values-ar.xml
│   │   │   │   │   │   │   ├── values-as/
│   │   │   │   │   │   │   │   └── values-as.xml
│   │   │   │   │   │   │   ├── values-az/
│   │   │   │   │   │   │   │   └── values-az.xml
│   │   │   │   │   │   │   ├── values-b+sr+Latn/
│   │   │   │   │   │   │   │   └── values-b+sr+Latn.xml
│   │   │   │   │   │   │   ├── values-be/
│   │   │   │   │   │   │   │   └── values-be.xml
│   │   │   │   │   │   │   ├── values-bg/
│   │   │   │   │   │   │   │   └── values-bg.xml
│   │   │   │   │   │   │   ├── values-bn/
│   │   │   │   │   │   │   │   └── values-bn.xml
│   │   │   │   │   │   │   ├── values-bs/
│   │   │   │   │   │   │   │   └── values-bs.xml
│   │   │   │   │   │   │   ├── values-ca/
│   │   │   │   │   │   │   │   └── values-ca.xml
│   │   │   │   │   │   │   ├── values-cs/
│   │   │   │   │   │   │   │   └── values-cs.xml
│   │   │   │   │   │   │   ├── values-da/
│   │   │   │   │   │   │   │   └── values-da.xml
│   │   │   │   │   │   │   ├── values-de/
│   │   │   │   │   │   │   │   └── values-de.xml
│   │   │   │   │   │   │   ├── values-el/
│   │   │   │   │   │   │   │   └── values-el.xml
│   │   │   │   │   │   │   ├── values-en-rAU/
│   │   │   │   │   │   │   │   └── values-en-rAU.xml
│   │   │   │   │   │   │   ├── values-en-rCA/
│   │   │   │   │   │   │   │   └── values-en-rCA.xml
│   │   │   │   │   │   │   ├── values-en-rGB/
│   │   │   │   │   │   │   │   └── values-en-rGB.xml
│   │   │   │   │   │   │   ├── values-en-rIN/
│   │   │   │   │   │   │   │   └── values-en-rIN.xml
│   │   │   │   │   │   │   ├── values-en-rXC/
│   │   │   │   │   │   │   │   └── values-en-rXC.xml
│   │   │   │   │   │   │   ├── values-es/
│   │   │   │   │   │   │   │   └── values-es.xml
│   │   │   │   │   │   │   ├── values-es-rUS/
│   │   │   │   │   │   │   │   └── values-es-rUS.xml
│   │   │   │   │   │   │   ├── values-et/
│   │   │   │   │   │   │   │   └── values-et.xml
│   │   │   │   │   │   │   ├── values-eu/
│   │   │   │   │   │   │   │   └── values-eu.xml
│   │   │   │   │   │   │   ├── values-fa/
│   │   │   │   │   │   │   │   └── values-fa.xml
│   │   │   │   │   │   │   ├── values-fi/
│   │   │   │   │   │   │   │   └── values-fi.xml
│   │   │   │   │   │   │   ├── values-fr/
│   │   │   │   │   │   │   │   └── values-fr.xml
│   │   │   │   │   │   │   ├── values-fr-rCA/
│   │   │   │   │   │   │   │   └── values-fr-rCA.xml
│   │   │   │   │   │   │   ├── values-gl/
│   │   │   │   │   │   │   │   └── values-gl.xml
│   │   │   │   │   │   │   ├── values-gu/
│   │   │   │   │   │   │   │   └── values-gu.xml
│   │   │   │   │   │   │   ├── values-hi/
│   │   │   │   │   │   │   │   └── values-hi.xml
│   │   │   │   │   │   │   ├── values-hr/
│   │   │   │   │   │   │   │   └── values-hr.xml
│   │   │   │   │   │   │   ├── values-hu/
│   │   │   │   │   │   │   │   └── values-hu.xml
│   │   │   │   │   │   │   ├── values-hy/
│   │   │   │   │   │   │   │   └── values-hy.xml
│   │   │   │   │   │   │   ├── values-in/
│   │   │   │   │   │   │   │   └── values-in.xml
│   │   │   │   │   │   │   ├── values-is/
│   │   │   │   │   │   │   │   └── values-is.xml
│   │   │   │   │   │   │   ├── values-it/
│   │   │   │   │   │   │   │   └── values-it.xml
│   │   │   │   │   │   │   ├── values-iw/
│   │   │   │   │   │   │   │   └── values-iw.xml
│   │   │   │   │   │   │   ├── values-ja/
│   │   │   │   │   │   │   │   └── values-ja.xml
│   │   │   │   │   │   │   ├── values-ka/
│   │   │   │   │   │   │   │   └── values-ka.xml
│   │   │   │   │   │   │   ├── values-kk/
│   │   │   │   │   │   │   │   └── values-kk.xml
│   │   │   │   │   │   │   ├── values-km/
│   │   │   │   │   │   │   │   └── values-km.xml
│   │   │   │   │   │   │   ├── values-kn/
│   │   │   │   │   │   │   │   └── values-kn.xml
│   │   │   │   │   │   │   ├── values-ko/
│   │   │   │   │   │   │   │   └── values-ko.xml
│   │   │   │   │   │   │   ├── values-ky/
│   │   │   │   │   │   │   │   └── values-ky.xml
│   │   │   │   │   │   │   ├── values-lo/
│   │   │   │   │   │   │   │   └── values-lo.xml
│   │   │   │   │   │   │   ├── values-lt/
│   │   │   │   │   │   │   │   └── values-lt.xml
│   │   │   │   │   │   │   ├── values-lv/
│   │   │   │   │   │   │   │   └── values-lv.xml
│   │   │   │   │   │   │   ├── values-mk/
│   │   │   │   │   │   │   │   └── values-mk.xml
│   │   │   │   │   │   │   ├── values-ml/
│   │   │   │   │   │   │   │   └── values-ml.xml
│   │   │   │   │   │   │   ├── values-mn/
│   │   │   │   │   │   │   │   └── values-mn.xml
│   │   │   │   │   │   │   ├── values-mr/
│   │   │   │   │   │   │   │   └── values-mr.xml
│   │   │   │   │   │   │   ├── values-ms/
│   │   │   │   │   │   │   │   └── values-ms.xml
│   │   │   │   │   │   │   ├── values-my/
│   │   │   │   │   │   │   │   └── values-my.xml
│   │   │   │   │   │   │   ├── values-nb/
│   │   │   │   │   │   │   │   └── values-nb.xml
│   │   │   │   │   │   │   ├── values-ne/
│   │   │   │   │   │   │   │   └── values-ne.xml
│   │   │   │   │   │   │   ├── values-night-v33/
│   │   │   │   │   │   │   │   └── values-night-v33.xml
│   │   │   │   │   │   │   ├── values-night-v8/
│   │   │   │   │   │   │   │   └── values-night-v8.xml
│   │   │   │   │   │   │   ├── values-nl/
│   │   │   │   │   │   │   │   └── values-nl.xml
│   │   │   │   │   │   │   ├── values-or/
│   │   │   │   │   │   │   │   └── values-or.xml
│   │   │   │   │   │   │   ├── values-pa/
│   │   │   │   │   │   │   │   └── values-pa.xml
│   │   │   │   │   │   │   ├── values-pl/
│   │   │   │   │   │   │   │   └── values-pl.xml
│   │   │   │   │   │   │   ├── values-pt/
│   │   │   │   │   │   │   │   └── values-pt.xml
│   │   │   │   │   │   │   ├── values-pt-rBR/
│   │   │   │   │   │   │   │   └── values-pt-rBR.xml
│   │   │   │   │   │   │   ├── values-pt-rPT/
│   │   │   │   │   │   │   │   └── values-pt-rPT.xml
│   │   │   │   │   │   │   ├── values-ro/
│   │   │   │   │   │   │   │   └── values-ro.xml
│   │   │   │   │   │   │   ├── values-ru/
│   │   │   │   │   │   │   │   └── values-ru.xml
│   │   │   │   │   │   │   ├── values-si/
│   │   │   │   │   │   │   │   └── values-si.xml
│   │   │   │   │   │   │   ├── values-sk/
│   │   │   │   │   │   │   │   └── values-sk.xml
│   │   │   │   │   │   │   ├── values-sl/
│   │   │   │   │   │   │   │   └── values-sl.xml
│   │   │   │   │   │   │   ├── values-sq/
│   │   │   │   │   │   │   │   └── values-sq.xml
│   │   │   │   │   │   │   ├── values-sr/
│   │   │   │   │   │   │   │   └── values-sr.xml
│   │   │   │   │   │   │   ├── values-sv/
│   │   │   │   │   │   │   │   └── values-sv.xml
│   │   │   │   │   │   │   ├── values-sw/
│   │   │   │   │   │   │   │   └── values-sw.xml
│   │   │   │   │   │   │   ├── values-ta/
│   │   │   │   │   │   │   │   └── values-ta.xml
│   │   │   │   │   │   │   ├── values-te/
│   │   │   │   │   │   │   │   └── values-te.xml
│   │   │   │   │   │   │   ├── values-th/
│   │   │   │   │   │   │   │   └── values-th.xml
│   │   │   │   │   │   │   ├── values-tl/
│   │   │   │   │   │   │   │   └── values-tl.xml
│   │   │   │   │   │   │   ├── values-tr/
│   │   │   │   │   │   │   │   └── values-tr.xml
│   │   │   │   │   │   │   ├── values-uk/
│   │   │   │   │   │   │   │   └── values-uk.xml
│   │   │   │   │   │   │   ├── values-ur/
│   │   │   │   │   │   │   │   └── values-ur.xml
│   │   │   │   │   │   │   ├── values-uz/
│   │   │   │   │   │   │   │   └── values-uz.xml
│   │   │   │   │   │   │   ├── values-v21/
│   │   │   │   │   │   │   │   └── values-v21.xml
│   │   │   │   │   │   │   ├── values-v27/
│   │   │   │   │   │   │   │   └── values-v27.xml
│   │   │   │   │   │   │   ├── values-v29/
│   │   │   │   │   │   │   │   └── values-v29.xml
│   │   │   │   │   │   │   ├── values-v30/
│   │   │   │   │   │   │   │   └── values-v30.xml
│   │   │   │   │   │   │   ├── values-v31/
│   │   │   │   │   │   │   │   └── values-v31.xml
│   │   │   │   │   │   │   ├── values-v33/
│   │   │   │   │   │   │   │   └── values-v33.xml
│   │   │   │   │   │   │   ├── values-vi/
│   │   │   │   │   │   │   │   └── values-vi.xml
│   │   │   │   │   │   │   ├── values-watch-v20/
│   │   │   │   │   │   │   │   └── values-watch-v20.xml
│   │   │   │   │   │   │   ├── values-zh-rCN/
│   │   │   │   │   │   │   │   └── values-zh-rCN.xml
│   │   │   │   │   │   │   ├── values-zh-rHK/
│   │   │   │   │   │   │   │   └── values-zh-rHK.xml
│   │   │   │   │   │   │   ├── values-zh-rTW/
│   │   │   │   │   │   │   │   └── values-zh-rTW.xml
│   │   │   │   │   │   │   └── values-zu/
│   │   │   │   │   │   │       └── values-zu.xml
│   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   └── packageDebugResources/
│   │   │   │   │       ├── merged.dir/values/
│   │   │   │   │       │   └── values.xml
│   │   │   │   │       ├── stripped.dir/
│   │   │   │   │       │   │   │   │   │       └── merger.xml
│   │   │   │   ├── debug-mergeJavaRes/
│   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   ├── ++ZYGmX2t7PcjH7eKxKYFw==
│   │   │   │   │   │   ├── +eU8kOrWgxkVut8FhvQiCg==
│   │   │   │   │   │   ├── 0J0gESDGG0jpKBjLRYms6w==
│   │   │   │   │   │   ├── 1dVspu7M8ilj+uS86NsPxg==
│   │   │   │   │   │   ├── 1eYbv8AVCsFds8pZdrvyNQ==
│   │   │   │   │   │   ├── 2bipfkPr24JOzseeEIvjTQ==
│   │   │   │   │   │   ├── 2y60ryomedBWnO2ITFGPhg==
│   │   │   │   │   │   ├── 3pP3fid38Gh8K0Xoj_1yYw==
│   │   │   │   │   │   ├── 3u2W6299ZrshJK2NXCvClg==
│   │   │   │   │   │   ├── 4h13cllF+_EMcXA6RYVBfA==
│   │   │   │   │   │   ├── 4nEYOlmgEF4IopAQ1B+Ubg==
│   │   │   │   │   │   ├── 5UuZj3k26dAKy6BKs51O1A==
│   │   │   │   │   │   ├── 6hq_wBfy576OvQ7Ltxg+tg==
│   │   │   │   │   │   ├── 7SiTtV0jVc5dXOcnYI1bcw==
│   │   │   │   │   │   ├── 81bcqTwCddaIWco6Z4Z9JQ==
│   │   │   │   │   │   ├── 8oZONRvLXaibdslAixfcdg==
│   │   │   │   │   │   ├── _9A+2_1saeePWzMn2y3kmg==
│   │   │   │   │   │   ├── _hdAXZS0wZVQGsNJYelpuw==
│   │   │   │   │   │   ├── _ikKWBbuKfXw3I6kzB8HvA==
│   │   │   │   │   │   ├── a1VeJCaoGk9HEivUFqeOYA==
│   │   │   │   │   │   ├── A9cSZB1LTcw+_9i9O4+zqw==
│   │   │   │   │   │   ├── AiKYedT3UhqfVakOtA2Ymw==
│   │   │   │   │   │   ├── AKkDhYjaYnHuz28PrQcoAA==
│   │   │   │   │   │   ├── Aum8RyvyKLwxGsZvF8wZew==
│   │   │   │   │   │   ├── AxrFpzc1Dc8H2NFDSV+5hA==
│   │   │   │   │   │   ├── AZ5lE7_tYWTc7mKB7cesZg==
│   │   │   │   │   │   ├── B081BuIDaWSo5McNdeDOlg==
│   │   │   │   │   │   ├── bDLGO34isE9c2h76dKH_dQ==
│   │   │   │   │   │   ├── BIO8B+IR6k405DzX3KVLfg==
│   │   │   │   │   │   ├── Bm73fd+UlT40_EQ4AfhIpQ==
│   │   │   │   │   │   ├── bML+DqklEZ+AsUXscK_DcA==
│   │   │   │   │   │   ├── BZh6gprMraiQS8cVYC8eqg==
│   │   │   │   │   │   ├── cobes8m6Hk9o89fLDICO4Q==
│   │   │   │   │   │   ├── DkA7dGodUL1SSxlq0HVAdA==
│   │   │   │   │   │   ├── dqTa20w+iEjsA_EAE3eC4Q==
│   │   │   │   │   │   ├── e34hP4DrupmSi_OLHoAavw==
│   │   │   │   │   │   ├── eAb6b17AoioR+wuvsUlXUg==
│   │   │   │   │   │   ├── fbJN1jTpNAQTBDjunGpNNw==
│   │   │   │   │   │   ├── fddcPQt+X0rwj1u0rasLjQ==
│   │   │   │   │   │   ├── FdRh4F1RmW0ZLDuB2MlFrA==
│   │   │   │   │   │   ├── FEsY314cXOuwQTw3HxBKKw==
│   │   │   │   │   │   ├── FGu9faub7ac3ItftqKwXqw==
│   │   │   │   │   │   ├── fo6z1aMqVMDTYAmMB2GVzw==
│   │   │   │   │   │   ├── FyiJWPkrgrwBc9ZSwusQnA==
│   │   │   │   │   │   ├── G1tS68HjVeiWmUxbawaJPA==
│   │   │   │   │   │   ├── gcynXIsM8n5S7Zg+JbHBzg==
│   │   │   │   │   │   ├── GeACj+sZn3H_sjbh9z2KFw==
│   │   │   │   │   │   ├── gMUj5N1rwbVTZNnhBOwS4w==
│   │   │   │   │   │   ├── GPZ3hPyjYQtqHVVbO98aJA==
│   │   │   │   │   │   ├── HVpgb4AbOhpJ2Je5_Nmzvw==
│   │   │   │   │   │   ├── irnRya5fxH2vYBDba_+Lyw==
│   │   │   │   │   │   ├── IxZ3r7b5eOyncEz6+ALO8w==
│   │   │   │   │   │   ├── iyHVm7BA1YcKVopQ02mrOg==
│   │   │   │   │   │   ├── JcTSvixFZnXWcWZkXLJZlQ==
│   │   │   │   │   │   ├── jcx91LYtRSSylx3FqtmsSw==
│   │   │   │   │   │   ├── jDGSIyhwljJd1DKRysktgA==
│   │   │   │   │   │   ├── KAatZja1KJrgZKJehZOIZA==
│   │   │   │   │   │   ├── KJ62grue6PFfapE8wOP5PA==
│   │   │   │   │   │   ├── KJ9nMJZHLreafzyVvUvT1Q==
│   │   │   │   │   │   ├── kKPfGcYe+SWk3HkVWX3hLg==
│   │   │   │   │   │   ├── kR+fYNMsWmdHYKd570bNeQ==
│   │   │   │   │   │   ├── Kws7Ph2jc1f3d_ecLLw8yA==
│   │   │   │   │   │   ├── L1_U19drPeYAOf5OUEH5pA==
│   │   │   │   │   │   ├── lAMrcFGaTpaTmTGFdijIAQ==
│   │   │   │   │   │   ├── LLpvg+2FGanbanSL7+AOGg==
│   │   │   │   │   │   ├── LNt3hjLZ01T1hFqRX95Mww==
│   │   │   │   │   │   ├── LrpPxC80P19GLVnwX_SXyg==
│   │   │   │   │   │   ├── LZ58n4Oq3_BpyNQ4dA75Eg==
│   │   │   │   │   │   ├── m7ifgdNNq+vMZDPtmarc1Q==
│   │   │   │   │   │   ├── mCrrONfKJ8D0TPM+BrBC+w==
│   │   │   │   │   │   ├── MGjkoJZVhVbQnzYuPyFT2g==
│   │   │   │   │   │   ├── MnSEDLpPhH5KrtR32crNcA==
│   │   │   │   │   │   ├── N4JbPtX00mbgf1JBj8hLpA==
│   │   │   │   │   │   ├── nmLHxSDr+xCoxOdocCTtnA==
│   │   │   │   │   │   ├── nNnABVdIqbuBQTX_5Q7A5Q==
│   │   │   │   │   │   ├── nZmeJWlo+8w2GZE9JVXkNA==
│   │   │   │   │   │   ├── oebJKCN25ui3SjFc6SEViw==
│   │   │   │   │   │   ├── OGmjon7NzrEtEFf3ZeqiXQ==
│   │   │   │   │   │   ├── oX2L7paC_Xb5Zc3A+LmXnA==
│   │   │   │   │   │   ├── pruEkOEQmabGaufVaz4OYQ==
│   │   │   │   │   │   ├── pRY76KU5StwLo498u8zFlw==
│   │   │   │   │   │   ├── PSo0SzzlB5g53H70D8Nsqw==
│   │   │   │   │   │   ├── qLOeJgBb4XAopiocjvOoPA==
│   │   │   │   │   │   ├── qlr5sAceP9eHkOvExf4xXw==
│   │   │   │   │   │   ├── QzzfJ0RNNRj0tK7tKwAIHw==
│   │   │   │   │   │   ├── RGz+pyX57SWpawlfzWqw+w==
│   │   │   │   │   │   ├── rJNtyKWG6wNPH3yX5TJOjQ==
│   │   │   │   │   │   ├── rwVdPbkFuNQzAySC0toqTQ==
│   │   │   │   │   │   ├── SfpbELK6Ns4ZtRuf40S+vg==
│   │   │   │   │   │   ├── SfYWWgymQvAfJ8ZrfXkj6A==
│   │   │   │   │   │   ├── spa6dzh8I6t5tS8vRvQphg==
│   │   │   │   │   │   ├── SVp+x2IW5trMlVHyl6JKpA==
│   │   │   │   │   │   ├── Tfr9alzGPmJZyNdGAJ34_A==
│   │   │   │   │   │   ├── uop6mUhhuMQPXlyq25GWOg==
│   │   │   │   │   │   ├── uy9KNfQai52a_U_lWEfkEQ==
│   │   │   │   │   │   ├── uz0U9XSEd9wjJai9Af6uJw==
│   │   │   │   │   │   ├── v1FFNCoEO5or6_tvb1ZiNA==
│   │   │   │   │   │   ├── V8DxNbbYWglX3HsdJ5bXKg==
│   │   │   │   │   │   ├── VsaW2m59uIx1BcQSyPbjzA==
│   │   │   │   │   │   ├── Vt0qPs9UjtEXIy6Mn6Z2cA==
│   │   │   │   │   │   ├── VTxpne0LDFl4QbPnqb9LBg==
│   │   │   │   │   │   ├── vzeix2EeVN_FlAoHcQVwTw==
│   │   │   │   │   │   ├── wfyzDzPNHrWUcZ2nnHY0aQ==
│   │   │   │   │   │   ├── Wo9tb4IoRx_Bg20yuchD2w==
│   │   │   │   │   │   ├── WyDzmK8eAXtfYHcSqQMo6Q==
│   │   │   │   │   │   ├── XHX9OWCcrnT0GNWceYvbHQ==
│   │   │   │   │   │   ├── XJ1PEoDvFDoM2eoSEGht7g==
│   │   │   │   │   │   ├── Xt6D_upDD6F2CVblQbGQSQ==
│   │   │   │   │   │   ├── yqvuQ1lcf0Zf_6rhnCBnMw==
│   │   │   │   │   │   ├── ZM9HFrNLU61QUDonCrDJWg==
│   │   │   │   │   │   └── ZoNGFjMrJJFr5GkYqx8JWA==
│   │   │   │   │   └── merge-state
│   │   │   │   ├── debugAndroidTest/
│   │   │   │   │   ├── mergeDebugAndroidTestResources/
│   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   │   ├── values/
│   │   │   │   │   │   │   │   └── values.xml
│   │   │   │   │   │   │   ├── values-af/
│   │   │   │   │   │   │   │   └── values-af.xml
│   │   │   │   │   │   │   ├── values-am/
│   │   │   │   │   │   │   │   └── values-am.xml
│   │   │   │   │   │   │   ├── values-ar/
│   │   │   │   │   │   │   │   └── values-ar.xml
│   │   │   │   │   │   │   ├── values-as/
│   │   │   │   │   │   │   │   └── values-as.xml
│   │   │   │   │   │   │   ├── values-az/
│   │   │   │   │   │   │   │   └── values-az.xml
│   │   │   │   │   │   │   ├── values-b+sr+Latn/
│   │   │   │   │   │   │   │   └── values-b+sr+Latn.xml
│   │   │   │   │   │   │   ├── values-be/
│   │   │   │   │   │   │   │   └── values-be.xml
│   │   │   │   │   │   │   ├── values-bg/
│   │   │   │   │   │   │   │   └── values-bg.xml
│   │   │   │   │   │   │   ├── values-bn/
│   │   │   │   │   │   │   │   └── values-bn.xml
│   │   │   │   │   │   │   ├── values-bs/
│   │   │   │   │   │   │   │   └── values-bs.xml
│   │   │   │   │   │   │   ├── values-ca/
│   │   │   │   │   │   │   │   └── values-ca.xml
│   │   │   │   │   │   │   ├── values-cs/
│   │   │   │   │   │   │   │   └── values-cs.xml
│   │   │   │   │   │   │   ├── values-da/
│   │   │   │   │   │   │   │   └── values-da.xml
│   │   │   │   │   │   │   ├── values-de/
│   │   │   │   │   │   │   │   └── values-de.xml
│   │   │   │   │   │   │   ├── values-el/
│   │   │   │   │   │   │   │   └── values-el.xml
│   │   │   │   │   │   │   ├── values-en-rAU/
│   │   │   │   │   │   │   │   └── values-en-rAU.xml
│   │   │   │   │   │   │   ├── values-en-rCA/
│   │   │   │   │   │   │   │   └── values-en-rCA.xml
│   │   │   │   │   │   │   ├── values-en-rGB/
│   │   │   │   │   │   │   │   └── values-en-rGB.xml
│   │   │   │   │   │   │   ├── values-en-rIN/
│   │   │   │   │   │   │   │   └── values-en-rIN.xml
│   │   │   │   │   │   │   ├── values-en-rXC/
│   │   │   │   │   │   │   │   └── values-en-rXC.xml
│   │   │   │   │   │   │   ├── values-es/
│   │   │   │   │   │   │   │   └── values-es.xml
│   │   │   │   │   │   │   ├── values-es-rUS/
│   │   │   │   │   │   │   │   └── values-es-rUS.xml
│   │   │   │   │   │   │   ├── values-et/
│   │   │   │   │   │   │   │   └── values-et.xml
│   │   │   │   │   │   │   ├── values-eu/
│   │   │   │   │   │   │   │   └── values-eu.xml
│   │   │   │   │   │   │   ├── values-fa/
│   │   │   │   │   │   │   │   └── values-fa.xml
│   │   │   │   │   │   │   ├── values-fi/
│   │   │   │   │   │   │   │   └── values-fi.xml
│   │   │   │   │   │   │   ├── values-fr/
│   │   │   │   │   │   │   │   └── values-fr.xml
│   │   │   │   │   │   │   ├── values-fr-rCA/
│   │   │   │   │   │   │   │   └── values-fr-rCA.xml
│   │   │   │   │   │   │   ├── values-gl/
│   │   │   │   │   │   │   │   └── values-gl.xml
│   │   │   │   │   │   │   ├── values-gu/
│   │   │   │   │   │   │   │   └── values-gu.xml
│   │   │   │   │   │   │   ├── values-hi/
│   │   │   │   │   │   │   │   └── values-hi.xml
│   │   │   │   │   │   │   ├── values-hr/
│   │   │   │   │   │   │   │   └── values-hr.xml
│   │   │   │   │   │   │   ├── values-hu/
│   │   │   │   │   │   │   │   └── values-hu.xml
│   │   │   │   │   │   │   ├── values-hy/
│   │   │   │   │   │   │   │   └── values-hy.xml
│   │   │   │   │   │   │   ├── values-in/
│   │   │   │   │   │   │   │   └── values-in.xml
│   │   │   │   │   │   │   ├── values-is/
│   │   │   │   │   │   │   │   └── values-is.xml
│   │   │   │   │   │   │   ├── values-it/
│   │   │   │   │   │   │   │   └── values-it.xml
│   │   │   │   │   │   │   ├── values-iw/
│   │   │   │   │   │   │   │   └── values-iw.xml
│   │   │   │   │   │   │   ├── values-ja/
│   │   │   │   │   │   │   │   └── values-ja.xml
│   │   │   │   │   │   │   ├── values-ka/
│   │   │   │   │   │   │   │   └── values-ka.xml
│   │   │   │   │   │   │   ├── values-kk/
│   │   │   │   │   │   │   │   └── values-kk.xml
│   │   │   │   │   │   │   ├── values-km/
│   │   │   │   │   │   │   │   └── values-km.xml
│   │   │   │   │   │   │   ├── values-kn/
│   │   │   │   │   │   │   │   └── values-kn.xml
│   │   │   │   │   │   │   ├── values-ko/
│   │   │   │   │   │   │   │   └── values-ko.xml
│   │   │   │   │   │   │   ├── values-ky/
│   │   │   │   │   │   │   │   └── values-ky.xml
│   │   │   │   │   │   │   ├── values-lo/
│   │   │   │   │   │   │   │   └── values-lo.xml
│   │   │   │   │   │   │   ├── values-lt/
│   │   │   │   │   │   │   │   └── values-lt.xml
│   │   │   │   │   │   │   ├── values-lv/
│   │   │   │   │   │   │   │   └── values-lv.xml
│   │   │   │   │   │   │   ├── values-mk/
│   │   │   │   │   │   │   │   └── values-mk.xml
│   │   │   │   │   │   │   ├── values-ml/
│   │   │   │   │   │   │   │   └── values-ml.xml
│   │   │   │   │   │   │   ├── values-mn/
│   │   │   │   │   │   │   │   └── values-mn.xml
│   │   │   │   │   │   │   ├── values-mr/
│   │   │   │   │   │   │   │   └── values-mr.xml
│   │   │   │   │   │   │   ├── values-ms/
│   │   │   │   │   │   │   │   └── values-ms.xml
│   │   │   │   │   │   │   ├── values-my/
│   │   │   │   │   │   │   │   └── values-my.xml
│   │   │   │   │   │   │   ├── values-nb/
│   │   │   │   │   │   │   │   └── values-nb.xml
│   │   │   │   │   │   │   ├── values-ne/
│   │   │   │   │   │   │   │   └── values-ne.xml
│   │   │   │   │   │   │   ├── values-nl/
│   │   │   │   │   │   │   │   └── values-nl.xml
│   │   │   │   │   │   │   ├── values-or/
│   │   │   │   │   │   │   │   └── values-or.xml
│   │   │   │   │   │   │   ├── values-pa/
│   │   │   │   │   │   │   │   └── values-pa.xml
│   │   │   │   │   │   │   ├── values-pl/
│   │   │   │   │   │   │   │   └── values-pl.xml
│   │   │   │   │   │   │   ├── values-pt/
│   │   │   │   │   │   │   │   └── values-pt.xml
│   │   │   │   │   │   │   ├── values-pt-rBR/
│   │   │   │   │   │   │   │   └── values-pt-rBR.xml
│   │   │   │   │   │   │   ├── values-pt-rPT/
│   │   │   │   │   │   │   │   └── values-pt-rPT.xml
│   │   │   │   │   │   │   ├── values-ro/
│   │   │   │   │   │   │   │   └── values-ro.xml
│   │   │   │   │   │   │   ├── values-ru/
│   │   │   │   │   │   │   │   └── values-ru.xml
│   │   │   │   │   │   │   ├── values-si/
│   │   │   │   │   │   │   │   └── values-si.xml
│   │   │   │   │   │   │   ├── values-sk/
│   │   │   │   │   │   │   │   └── values-sk.xml
│   │   │   │   │   │   │   ├── values-sl/
│   │   │   │   │   │   │   │   └── values-sl.xml
│   │   │   │   │   │   │   ├── values-sq/
│   │   │   │   │   │   │   │   └── values-sq.xml
│   │   │   │   │   │   │   ├── values-sr/
│   │   │   │   │   │   │   │   └── values-sr.xml
│   │   │   │   │   │   │   ├── values-sv/
│   │   │   │   │   │   │   │   └── values-sv.xml
│   │   │   │   │   │   │   ├── values-sw/
│   │   │   │   │   │   │   │   └── values-sw.xml
│   │   │   │   │   │   │   ├── values-ta/
│   │   │   │   │   │   │   │   └── values-ta.xml
│   │   │   │   │   │   │   ├── values-te/
│   │   │   │   │   │   │   │   └── values-te.xml
│   │   │   │   │   │   │   ├── values-th/
│   │   │   │   │   │   │   │   └── values-th.xml
│   │   │   │   │   │   │   ├── values-tl/
│   │   │   │   │   │   │   │   └── values-tl.xml
│   │   │   │   │   │   │   ├── values-tr/
│   │   │   │   │   │   │   │   └── values-tr.xml
│   │   │   │   │   │   │   ├── values-uk/
│   │   │   │   │   │   │   │   └── values-uk.xml
│   │   │   │   │   │   │   ├── values-ur/
│   │   │   │   │   │   │   │   └── values-ur.xml
│   │   │   │   │   │   │   ├── values-uz/
│   │   │   │   │   │   │   │   └── values-uz.xml
│   │   │   │   │   │   │   ├── values-v18/
│   │   │   │   │   │   │   │   └── values-v18.xml
│   │   │   │   │   │   │   ├── values-v21/
│   │   │   │   │   │   │   │   └── values-v21.xml
│   │   │   │   │   │   │   ├── values-v28/
│   │   │   │   │   │   │   │   └── values-v28.xml
│   │   │   │   │   │   │   ├── values-vi/
│   │   │   │   │   │   │   │   └── values-vi.xml
│   │   │   │   │   │   │   ├── values-zh-rCN/
│   │   │   │   │   │   │   │   └── values-zh-rCN.xml
│   │   │   │   │   │   │   ├── values-zh-rHK/
│   │   │   │   │   │   │   │   └── values-zh-rHK.xml
│   │   │   │   │   │   │   ├── values-zh-rTW/
│   │   │   │   │   │   │   │   └── values-zh-rTW.xml
│   │   │   │   │   │   │   └── values-zu/
│   │   │   │   │   │   │       └── values-zu.xml
│   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   └── packageDebugAndroidTestResources/
│   │   │   │   │       ├── merged.dir/
│   │   │   │   │       ├── stripped.dir/
│   │   │   │   │       │   │   │   │   │       └── merger.xml
│   │   │   │   ├── debugAndroidTest-mergeJavaRes/
│   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   ├── 48At24EEFRZPC3CWa7Ya0w==
│   │   │   │   │   │   ├── 4eRbhTSaEGgcPBsTd8UA9A==
│   │   │   │   │   │   ├── 6hq_wBfy576OvQ7Ltxg+tg==
│   │   │   │   │   │   ├── aP0DPTtDB_GofRwCyc3K_A==
│   │   │   │   │   │   ├── c4odLSp7S20CYLLWc97Lgw==
│   │   │   │   │   │   ├── cjhgi5EDunsfKJ6uqXwVSg==
│   │   │   │   │   │   ├── eFgg1ojHO2aOtECJkG+9qg==
│   │   │   │   │   │   ├── g4mrZijK2lNSAd9luP0sAg==
│   │   │   │   │   │   ├── GxNiSm1FbL6kD+llErrplQ==
│   │   │   │   │   │   ├── JL446swB5G3sX3UMWguHew==
│   │   │   │   │   │   ├── jumJK7oonsYMi9bYoiCapw==
│   │   │   │   │   │   ├── LGqUhMq988qomxHuR4LwDA==
│   │   │   │   │   │   ├── M7LC7dkivuZb_JlNJgmv3g==
│   │   │   │   │   │   ├── Oze1vywFy8H5AXv8fWfOGw==
│   │   │   │   │   │   ├── p0t+jzauR85wvGvlgAKUtA==
│   │   │   │   │   │   ├── Q7efWnpQ0wvhA6djwPeNbg==
│   │   │   │   │   │   ├── QzzfJ0RNNRj0tK7tKwAIHw==
│   │   │   │   │   │   └── Uz3BITtYPEUaoF_jXXo5ug==
│   │   │   │   │   └── merge-state
│   │   │   │   ├── mergeDebugAndroidTestAssets/
│   │   │   │   │   └── merger.xml
│   │   │   │   ├── mergeDebugAndroidTestJniLibFolders/
│   │   │   │   │   └── merger.xml
│   │   │   │   ├── mergeDebugAssets/
│   │   │   │   │   └── merger.xml
│   │   │   │   ├── mergeDebugJniLibFolders/
│   │   │   │   │   └── merger.xml
│   │   │   │   ├── mergeReleaseAssets/
│   │   │   │   │   └── merger.xml
│   │   │   │   ├── mergeReleaseJniLibFolders/
│   │   │   │   │   └── merger.xml
│   │   │   │   ├── packageDebug/tmp/debug/
│   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   ├── androidResources
│   │   │   │   │   │   └── javaResources0
│   │   │   │   │   └── dex-renamer-state.txt
│   │   │   │   ├── packageDebugAndroidTest/tmp/debugAndroidTest/
│   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   ├── androidResources
│   │   │   │   │   │   └── javaResources0
│   │   │   │   │   └── dex-renamer-state.txt
│   │   │   │   ├── packageRelease/tmp/release/
│   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   ├── androidResources
│   │   │   │   │   │   └── javaResources0
│   │   │   │   │   └── dex-renamer-state.txt
│   │   │   │   ├── release/
│   │   │   │   │   ├── mergeReleaseResources/
│   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   │   ├── values/
│   │   │   │   │   │   │   │   └── values.xml
│   │   │   │   │   │   │   ├── values-af/
│   │   │   │   │   │   │   │   └── values-af.xml
│   │   │   │   │   │   │   ├── values-am/
│   │   │   │   │   │   │   │   └── values-am.xml
│   │   │   │   │   │   │   ├── values-ar/
│   │   │   │   │   │   │   │   └── values-ar.xml
│   │   │   │   │   │   │   ├── values-as/
│   │   │   │   │   │   │   │   └── values-as.xml
│   │   │   │   │   │   │   ├── values-az/
│   │   │   │   │   │   │   │   └── values-az.xml
│   │   │   │   │   │   │   ├── values-b+sr+Latn/
│   │   │   │   │   │   │   │   └── values-b+sr+Latn.xml
│   │   │   │   │   │   │   ├── values-be/
│   │   │   │   │   │   │   │   └── values-be.xml
│   │   │   │   │   │   │   ├── values-bg/
│   │   │   │   │   │   │   │   └── values-bg.xml
│   │   │   │   │   │   │   ├── values-bn/
│   │   │   │   │   │   │   │   └── values-bn.xml
│   │   │   │   │   │   │   ├── values-bs/
│   │   │   │   │   │   │   │   └── values-bs.xml
│   │   │   │   │   │   │   ├── values-ca/
│   │   │   │   │   │   │   │   └── values-ca.xml
│   │   │   │   │   │   │   ├── values-cs/
│   │   │   │   │   │   │   │   └── values-cs.xml
│   │   │   │   │   │   │   ├── values-da/
│   │   │   │   │   │   │   │   └── values-da.xml
│   │   │   │   │   │   │   ├── values-de/
│   │   │   │   │   │   │   │   └── values-de.xml
│   │   │   │   │   │   │   ├── values-el/
│   │   │   │   │   │   │   │   └── values-el.xml
│   │   │   │   │   │   │   ├── values-en-rAU/
│   │   │   │   │   │   │   │   └── values-en-rAU.xml
│   │   │   │   │   │   │   ├── values-en-rCA/
│   │   │   │   │   │   │   │   └── values-en-rCA.xml
│   │   │   │   │   │   │   ├── values-en-rGB/
│   │   │   │   │   │   │   │   └── values-en-rGB.xml
│   │   │   │   │   │   │   ├── values-en-rIN/
│   │   │   │   │   │   │   │   └── values-en-rIN.xml
│   │   │   │   │   │   │   ├── values-en-rXC/
│   │   │   │   │   │   │   │   └── values-en-rXC.xml
│   │   │   │   │   │   │   ├── values-es/
│   │   │   │   │   │   │   │   └── values-es.xml
│   │   │   │   │   │   │   ├── values-es-rUS/
│   │   │   │   │   │   │   │   └── values-es-rUS.xml
│   │   │   │   │   │   │   ├── values-et/
│   │   │   │   │   │   │   │   └── values-et.xml
│   │   │   │   │   │   │   ├── values-eu/
│   │   │   │   │   │   │   │   └── values-eu.xml
│   │   │   │   │   │   │   ├── values-fa/
│   │   │   │   │   │   │   │   └── values-fa.xml
│   │   │   │   │   │   │   ├── values-fi/
│   │   │   │   │   │   │   │   └── values-fi.xml
│   │   │   │   │   │   │   ├── values-fr/
│   │   │   │   │   │   │   │   └── values-fr.xml
│   │   │   │   │   │   │   ├── values-fr-rCA/
│   │   │   │   │   │   │   │   └── values-fr-rCA.xml
│   │   │   │   │   │   │   ├── values-gl/
│   │   │   │   │   │   │   │   └── values-gl.xml
│   │   │   │   │   │   │   ├── values-gu/
│   │   │   │   │   │   │   │   └── values-gu.xml
│   │   │   │   │   │   │   ├── values-hi/
│   │   │   │   │   │   │   │   └── values-hi.xml
│   │   │   │   │   │   │   ├── values-hr/
│   │   │   │   │   │   │   │   └── values-hr.xml
│   │   │   │   │   │   │   ├── values-hu/
│   │   │   │   │   │   │   │   └── values-hu.xml
│   │   │   │   │   │   │   ├── values-hy/
│   │   │   │   │   │   │   │   └── values-hy.xml
│   │   │   │   │   │   │   ├── values-in/
│   │   │   │   │   │   │   │   └── values-in.xml
│   │   │   │   │   │   │   ├── values-is/
│   │   │   │   │   │   │   │   └── values-is.xml
│   │   │   │   │   │   │   ├── values-it/
│   │   │   │   │   │   │   │   └── values-it.xml
│   │   │   │   │   │   │   ├── values-iw/
│   │   │   │   │   │   │   │   └── values-iw.xml
│   │   │   │   │   │   │   ├── values-ja/
│   │   │   │   │   │   │   │   └── values-ja.xml
│   │   │   │   │   │   │   ├── values-ka/
│   │   │   │   │   │   │   │   └── values-ka.xml
│   │   │   │   │   │   │   ├── values-kk/
│   │   │   │   │   │   │   │   └── values-kk.xml
│   │   │   │   │   │   │   ├── values-km/
│   │   │   │   │   │   │   │   └── values-km.xml
│   │   │   │   │   │   │   ├── values-kn/
│   │   │   │   │   │   │   │   └── values-kn.xml
│   │   │   │   │   │   │   ├── values-ko/
│   │   │   │   │   │   │   │   └── values-ko.xml
│   │   │   │   │   │   │   ├── values-ky/
│   │   │   │   │   │   │   │   └── values-ky.xml
│   │   │   │   │   │   │   ├── values-lo/
│   │   │   │   │   │   │   │   └── values-lo.xml
│   │   │   │   │   │   │   ├── values-lt/
│   │   │   │   │   │   │   │   └── values-lt.xml
│   │   │   │   │   │   │   ├── values-lv/
│   │   │   │   │   │   │   │   └── values-lv.xml
│   │   │   │   │   │   │   ├── values-mk/
│   │   │   │   │   │   │   │   └── values-mk.xml
│   │   │   │   │   │   │   ├── values-ml/
│   │   │   │   │   │   │   │   └── values-ml.xml
│   │   │   │   │   │   │   ├── values-mn/
│   │   │   │   │   │   │   │   └── values-mn.xml
│   │   │   │   │   │   │   ├── values-mr/
│   │   │   │   │   │   │   │   └── values-mr.xml
│   │   │   │   │   │   │   ├── values-ms/
│   │   │   │   │   │   │   │   └── values-ms.xml
│   │   │   │   │   │   │   ├── values-my/
│   │   │   │   │   │   │   │   └── values-my.xml
│   │   │   │   │   │   │   ├── values-nb/
│   │   │   │   │   │   │   │   └── values-nb.xml
│   │   │   │   │   │   │   ├── values-ne/
│   │   │   │   │   │   │   │   └── values-ne.xml
│   │   │   │   │   │   │   ├── values-night-v33/
│   │   │   │   │   │   │   │   └── values-night-v33.xml
│   │   │   │   │   │   │   ├── values-night-v8/
│   │   │   │   │   │   │   │   └── values-night-v8.xml
│   │   │   │   │   │   │   ├── values-nl/
│   │   │   │   │   │   │   │   └── values-nl.xml
│   │   │   │   │   │   │   ├── values-or/
│   │   │   │   │   │   │   │   └── values-or.xml
│   │   │   │   │   │   │   ├── values-pa/
│   │   │   │   │   │   │   │   └── values-pa.xml
│   │   │   │   │   │   │   ├── values-pl/
│   │   │   │   │   │   │   │   └── values-pl.xml
│   │   │   │   │   │   │   ├── values-pt/
│   │   │   │   │   │   │   │   └── values-pt.xml
│   │   │   │   │   │   │   ├── values-pt-rBR/
│   │   │   │   │   │   │   │   └── values-pt-rBR.xml
│   │   │   │   │   │   │   ├── values-pt-rPT/
│   │   │   │   │   │   │   │   └── values-pt-rPT.xml
│   │   │   │   │   │   │   ├── values-ro/
│   │   │   │   │   │   │   │   └── values-ro.xml
│   │   │   │   │   │   │   ├── values-ru/
│   │   │   │   │   │   │   │   └── values-ru.xml
│   │   │   │   │   │   │   ├── values-si/
│   │   │   │   │   │   │   │   └── values-si.xml
│   │   │   │   │   │   │   ├── values-sk/
│   │   │   │   │   │   │   │   └── values-sk.xml
│   │   │   │   │   │   │   ├── values-sl/
│   │   │   │   │   │   │   │   └── values-sl.xml
│   │   │   │   │   │   │   ├── values-sq/
│   │   │   │   │   │   │   │   └── values-sq.xml
│   │   │   │   │   │   │   ├── values-sr/
│   │   │   │   │   │   │   │   └── values-sr.xml
│   │   │   │   │   │   │   ├── values-sv/
│   │   │   │   │   │   │   │   └── values-sv.xml
│   │   │   │   │   │   │   ├── values-sw/
│   │   │   │   │   │   │   │   └── values-sw.xml
│   │   │   │   │   │   │   ├── values-ta/
│   │   │   │   │   │   │   │   └── values-ta.xml
│   │   │   │   │   │   │   ├── values-te/
│   │   │   │   │   │   │   │   └── values-te.xml
│   │   │   │   │   │   │   ├── values-th/
│   │   │   │   │   │   │   │   └── values-th.xml
│   │   │   │   │   │   │   ├── values-tl/
│   │   │   │   │   │   │   │   └── values-tl.xml
│   │   │   │   │   │   │   ├── values-tr/
│   │   │   │   │   │   │   │   └── values-tr.xml
│   │   │   │   │   │   │   ├── values-uk/
│   │   │   │   │   │   │   │   └── values-uk.xml
│   │   │   │   │   │   │   ├── values-ur/
│   │   │   │   │   │   │   │   └── values-ur.xml
│   │   │   │   │   │   │   ├── values-uz/
│   │   │   │   │   │   │   │   └── values-uz.xml
│   │   │   │   │   │   │   ├── values-v21/
│   │   │   │   │   │   │   │   └── values-v21.xml
│   │   │   │   │   │   │   ├── values-v27/
│   │   │   │   │   │   │   │   └── values-v27.xml
│   │   │   │   │   │   │   ├── values-v29/
│   │   │   │   │   │   │   │   └── values-v29.xml
│   │   │   │   │   │   │   ├── values-v30/
│   │   │   │   │   │   │   │   └── values-v30.xml
│   │   │   │   │   │   │   ├── values-v31/
│   │   │   │   │   │   │   │   └── values-v31.xml
│   │   │   │   │   │   │   ├── values-v33/
│   │   │   │   │   │   │   │   └── values-v33.xml
│   │   │   │   │   │   │   ├── values-vi/
│   │   │   │   │   │   │   │   └── values-vi.xml
│   │   │   │   │   │   │   ├── values-watch-v20/
│   │   │   │   │   │   │   │   └── values-watch-v20.xml
│   │   │   │   │   │   │   ├── values-zh-rCN/
│   │   │   │   │   │   │   │   └── values-zh-rCN.xml
│   │   │   │   │   │   │   ├── values-zh-rHK/
│   │   │   │   │   │   │   │   └── values-zh-rHK.xml
│   │   │   │   │   │   │   ├── values-zh-rTW/
│   │   │   │   │   │   │   │   └── values-zh-rTW.xml
│   │   │   │   │   │   │   └── values-zu/
│   │   │   │   │   │   │       └── values-zu.xml
│   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   └── packageReleaseResources/
│   │   │   │   │       ├── merged.dir/values/
│   │   │   │   │       │   └── values.xml
│   │   │   │   │       ├── stripped.dir/
│   │   │   │   │       │   │   │   │   │       └── merger.xml
│   │   │   │   ├── release-mergeJavaRes/
│   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   ├── +cH4YVhnBaaNPH4ZLLUP+g==
│   │   │   │   │   │   ├── 0mId3OjauzkMk+Xj0e_YnA==
│   │   │   │   │   │   ├── 1mG0bHF9ug9LekzxGMjNcA==
│   │   │   │   │   │   ├── 2c1lrxonR5vRnT5Zi14zUw==
│   │   │   │   │   │   ├── 2oRvXpz69qQRulaL8es3_A==
│   │   │   │   │   │   ├── 3cDE5nGcU9hlrI+hghhnQA==
│   │   │   │   │   │   ├── 3xf7oZ+tUikZwbKADVqJ7w==
│   │   │   │   │   │   ├── 4tk5wDG0fujKu6e56Iz6aQ==
│   │   │   │   │   │   ├── 5nU0fYIdxpZG6k6KflfZ4g==
│   │   │   │   │   │   ├── 6bhANZHyERw_TG3ckDcSsw==
│   │   │   │   │   │   ├── 6FqwXlkC1vzyBua_I8KW4A==
│   │   │   │   │   │   ├── 6hq_wBfy576OvQ7Ltxg+tg==
│   │   │   │   │   │   ├── 82tf5c5spcVH4o8aM8auTQ==
│   │   │   │   │   │   ├── 8Jkof5_AsNqdZePEw_vNLw==
│   │   │   │   │   │   ├── _9A+2_1saeePWzMn2y3kmg==
│   │   │   │   │   │   ├── _hdAXZS0wZVQGsNJYelpuw==
│   │   │   │   │   │   ├── _SD+YP6aTzXhsLSQeJH4vw==
│   │   │   │   │   │   ├── A+LoIbs3WeKu9htzKdUPRQ==
│   │   │   │   │   │   ├── AKkDhYjaYnHuz28PrQcoAA==
│   │   │   │   │   │   ├── Aly2KDm5g4jG4efiOzC6Hg==
│   │   │   │   │   │   ├── aoHX1gse8__zS+SJPsxiUw==
│   │   │   │   │   │   ├── Aum8RyvyKLwxGsZvF8wZew==
│   │   │   │   │   │   ├── AxD1D5Th3uG7Zu8oA4Ky2w==
│   │   │   │   │   │   ├── AxrFpzc1Dc8H2NFDSV+5hA==
│   │   │   │   │   │   ├── B081BuIDaWSo5McNdeDOlg==
│   │   │   │   │   │   ├── b7Mn87ILS7dN5ujd1W05ug==
│   │   │   │   │   │   ├── bHNXMMJiQ6Jr9JvrNAiTaA==
│   │   │   │   │   │   ├── BIO8B+IR6k405DzX3KVLfg==
│   │   │   │   │   │   ├── Bm73fd+UlT40_EQ4AfhIpQ==
│   │   │   │   │   │   ├── DgjuFBfAChe9eQ8X8NIEbw==
│   │   │   │   │   │   ├── dhgeFntNwOo6CxAbvfIG6w==
│   │   │   │   │   │   ├── DkA7dGodUL1SSxlq0HVAdA==
│   │   │   │   │   │   ├── eAb6b17AoioR+wuvsUlXUg==
│   │   │   │   │   │   ├── evGm9dQcoXS72EQL9UOhEQ==
│   │   │   │   │   │   ├── f_JQddwr6Fwed4cLp0wetg==
│   │   │   │   │   │   ├── fbJN1jTpNAQTBDjunGpNNw==
│   │   │   │   │   │   ├── Fd0b39M_mlg9zmAhZB__7Q==
│   │   │   │   │   │   ├── fMTcPxrVdC0KoDsyVn4asA==
│   │   │   │   │   │   ├── G1tS68HjVeiWmUxbawaJPA==
│   │   │   │   │   │   ├── gENy4wv5_h1fUpLuoOjuzQ==
│   │   │   │   │   │   ├── gMUj5N1rwbVTZNnhBOwS4w==
│   │   │   │   │   │   ├── gUbEaEdPneA7Ack3ZkOy0Q==
│   │   │   │   │   │   ├── GY0ik8M4SCJROg7yInIy0A==
│   │   │   │   │   │   ├── hsSQkXraU2jrjjWFtnuvrg==
│   │   │   │   │   │   ├── hvT2SD4AEPDMLIElS+mHDA==
│   │   │   │   │   │   ├── ixX2wEWEgdea30xyT03EHw==
│   │   │   │   │   │   ├── jbxwRncZFjxCznOBqjOzVg==
│   │   │   │   │   │   ├── jqlJPm8usXz0cgggCe7cxw==
│   │   │   │   │   │   ├── kGIkQczgnDxdYl4J_98Yhg==
│   │   │   │   │   │   ├── KM0598noRbX_64nFHFyGbg==
│   │   │   │   │   │   ├── Knt+HiQAu38YkniT+OHtYw==
│   │   │   │   │   │   ├── Kws7Ph2jc1f3d_ecLLw8yA==
│   │   │   │   │   │   ├── KYgGY6pynYXZXVb1djJMAQ==
│   │   │   │   │   │   ├── lAMrcFGaTpaTmTGFdijIAQ==
│   │   │   │   │   │   ├── liDwnQWksQDGQkc6tbR3EQ==
│   │   │   │   │   │   ├── lP87e1PQrpsolCDNl_8zSg==
│   │   │   │   │   │   ├── LwsClbX8eg7D4ImVh1KZNA==
│   │   │   │   │   │   ├── m7ifgdNNq+vMZDPtmarc1Q==
│   │   │   │   │   │   ├── MGjkoJZVhVbQnzYuPyFT2g==
│   │   │   │   │   │   ├── MnSEDLpPhH5KrtR32crNcA==
│   │   │   │   │   │   ├── N4JbPtX00mbgf1JBj8hLpA==
│   │   │   │   │   │   ├── nmLHxSDr+xCoxOdocCTtnA==
│   │   │   │   │   │   ├── nV+0Lmt3O0st0MwsjOd9Tg==
│   │   │   │   │   │   ├── OAMBEH2RbZasCIg0obQ+0w==
│   │   │   │   │   │   ├── OJkmznVFwUVLCdW49zJufg==
│   │   │   │   │   │   ├── pdWKsTOWQeRvGhJ_WbbqcA==
│   │   │   │   │   │   ├── PPo8ES+XJJVg5EXdSPBtAw==
│   │   │   │   │   │   ├── Q1dTi7qEm7a5AHyw_SuKIA==
│   │   │   │   │   │   ├── qnTXY7OYGl_DyJV0XrbaJA==
│   │   │   │   │   │   ├── QoVA6KGI6V0uUFqSTsgzMg==
│   │   │   │   │   │   ├── QrG8dDG0fVuphkKB8lWP3Q==
│   │   │   │   │   │   ├── QTWpbmUy5h47YfGEWj+IMw==
│   │   │   │   │   │   ├── Quj+g9oyqc+CHfTcq_G4JA==
│   │   │   │   │   │   ├── QUJiTdwnP6g3KP_jWQtkNA==
│   │   │   │   │   │   ├── QzzfJ0RNNRj0tK7tKwAIHw==
│   │   │   │   │   │   ├── Rd3FnQWTJCPTgnJvuGnRdg==
│   │   │   │   │   │   ├── rFq7C8+nMGkwsorsWCh+0Q==
│   │   │   │   │   │   ├── RjZ++D7x+IuW7+GxaY72rA==
│   │   │   │   │   │   ├── RmKfDCEJN7yHVSyZQFIIQQ==
│   │   │   │   │   │   ├── RzPXzW7Zwc44VPJUiiGp9Q==
│   │   │   │   │   │   ├── SFDW7qdTDNMF_AhD3zDVzA==
│   │   │   │   │   │   ├── spa6dzh8I6t5tS8vRvQphg==
│   │   │   │   │   │   ├── ST45FlHqaQWhoytMik2bDA==
│   │   │   │   │   │   ├── tb0ESeUUr1dY+xXkZ+UIPg==
│   │   │   │   │   │   ├── TfIFAW+us4lEP+mn4w7r8g==
│   │   │   │   │   │   ├── u_0ofLGcfemPD7bJJQ+mYQ==
│   │   │   │   │   │   ├── uH8yd8dMKoWAg4ucpfGoTA==
│   │   │   │   │   │   ├── uM9DzpL45U_0ZlvwPeolBQ==
│   │   │   │   │   │   ├── v1FFNCoEO5or6_tvb1ZiNA==
│   │   │   │   │   │   ├── V8DxNbbYWglX3HsdJ5bXKg==
│   │   │   │   │   │   ├── vgAa3OKrkxTZ8qoh5H6Trg==
│   │   │   │   │   │   ├── vzeix2EeVN_FlAoHcQVwTw==
│   │   │   │   │   │   ├── wD9YoZ_6vAOkDRjVj4LasA==
│   │   │   │   │   │   ├── WqY7vFHEM5AuIBsxYmj+RA==
│   │   │   │   │   │   ├── WTtd_s1mEGW8pyYw9CjJeA==
│   │   │   │   │   │   ├── XfGoUyYQ7lqXj3mJqfZ9JA==
│   │   │   │   │   │   ├── xFWToeq2JaOd9ZjmjdvekQ==
│   │   │   │   │   │   ├── Xhqlt3Jug7gjJpDS9ZDI_Q==
│   │   │   │   │   │   ├── Xt6D_upDD6F2CVblQbGQSQ==
│   │   │   │   │   │   ├── xxRoj2BFggI8xE9ZgrF+dg==
│   │   │   │   │   │   ├── xyso7UbN2jxAxNXnIjUmSw==
│   │   │   │   │   │   ├── y7XEXG3QfLNhA1KGmVDZuQ==
│   │   │   │   │   │   ├── Y8a0JeWtsGd_o90hzUV4Xw==
│   │   │   │   │   │   ├── Zf_hpBrNMfWaErqZu6SiTQ==
│   │   │   │   │   │   ├── ziKH7+MJTy9RzhDzb74Imw==
│   │   │   │   │   │   └── zJBRAHIsGnUNI74aGx4n6Q==
│   │   │   │   │   └── merge-state
│   │   │   │   ├── transformDebugClassesWithAsm/
│   │   │   │   └── transformReleaseClassesWithAsm/
│   │   │   ├── java_res/
│   │   │   │   ├── debug/processDebugJavaRes/out/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/
│   │   │   │   │   │   ├── app/navigation/
│   │   │   │   │   │   └── ui/theme/
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── app.kotlin_module
│   │   │   │   ├── debugAndroidTest/processDebugAndroidTestJavaRes/out/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── app.kotlin_module
│   │   │   │   ├── debugUnitTest/processDebugUnitTestJavaRes/out/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── app.kotlin_module
│   │   │   │   └── release/processReleaseJavaRes/out/
│   │   │   │       ├── com/miguelrodriguez19/safecube/
│   │   │   │       │   ├── app/navigation/
│   │   │   │       │   └── ui/theme/
│   │   │   │       └── META-INF/
│   │   │   │           └── app.kotlin_module
│   │   │   ├── javac/
│   │   │   │   ├── debug/compileDebugJavaWithJavac/classes/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/
│   │   │   │   │   │   └── SafeCubeApp_GeneratedInjector.class
│   │   │   │   │   ├── dagger/hilt/internal/aggregatedroot/codegen/
│   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.class
│   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │       ├── _com_miguelrodriguez19_safecube_app_navigation_NavigationGatesEntryPoint.class
│   │   │   │   │       └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.class
│   │   │   │   └── release/compileReleaseJavaWithJavac/classes/
│   │   │   │       ├── com/miguelrodriguez19/safecube/
│   │   │   │       │   └── SafeCubeApp_GeneratedInjector.class
│   │   │   │       ├── dagger/hilt/internal/aggregatedroot/codegen/
│   │   │   │       │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.class
│   │   │   │       └── hilt_aggregated_deps/
│   │   │   │           ├── _com_miguelrodriguez19_safecube_app_navigation_NavigationGatesEntryPoint.class
│   │   │   │           └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.class
│   │   │   ├── linked_resources_binary_format/
│   │   │   │   ├── debug/processDebugResources/
│   │   │   │   │   ├── linked-resources-binary-format-debug.ap_
│   │   │   │   │   └── output-metadata.json
│   │   │   │   └── debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │       ├── linked-resources-binary-format.ap_
│   │   │   │       └── output-metadata.json
│   │   │   ├── linked_resources_proto_format/release/processReleaseResources/
│   │   │   │   ├── linked-resources-proto-format-release.ap_
│   │   │   │   └── output-metadata.json
│   │   │   ├── local_only_symbol_list/
│   │   │   │   ├── debug/parseDebugLocalResources/
│   │   │   │   │   └── R-def.txt
│   │   │   │   ├── debugAndroidTest/parseDebugAndroidTestLocalResources/
│   │   │   │   │   └── R-def.txt
│   │   │   │   └── release/parseReleaseLocalResources/
│   │   │   │       └── R-def.txt
│   │   │   ├── manifest_merge_blame_file/
│   │   │   │   ├── debug/processDebugMainManifest/
│   │   │   │   │   └── manifest-merger-blame-debug-report.txt
│   │   │   │   ├── debugAndroidTest/processDebugAndroidTestManifest/
│   │   │   │   │   └── manifest-merger-blame-debug-androidTest-report.txt
│   │   │   │   ├── debugUnitTest/processDebugUnitTestManifest/
│   │   │   │   │   └── manifest-merger-blame-debug-test-report.txt
│   │   │   │   └── release/processReleaseMainManifest/
│   │   │   │       └── manifest-merger-blame-release-report.txt
│   │   │   ├── mapping/release/minifyReleaseWithR8/
│   │   │   │   ├── configuration.txt
│   │   │   │   ├── mapping.txt
│   │   │   │   ├── resources.txt
│   │   │   │   ├── seeds.txt
│   │   │   │   └── usage.txt
│   │   │   ├── merged-not-compiled-resources/release/
│   │   │   │   ├── anim-v21/
│   │   │   │   │   └── fragment_fast_out_extra_slow_in.xml
│   │   │   │   ├── animator/
│   │   │   │   │   ├── fragment_close_enter.xml
│   │   │   │   │   ├── fragment_close_exit.xml
│   │   │   │   │   ├── fragment_fade_enter.xml
│   │   │   │   │   ├── fragment_fade_exit.xml
│   │   │   │   │   ├── fragment_open_enter.xml
│   │   │   │   │   └── fragment_open_exit.xml
│   │   │   │   ├── color/
│   │   │   │   │   ├── vector_tint_color.xml
│   │   │   │   │   └── vector_tint_theme_color.xml
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── abc_vector_test.xml
│   │   │   │   │   ├── ic_call_answer.xml
│   │   │   │   │   ├── ic_call_answer_low.xml
│   │   │   │   │   ├── ic_call_answer_video.xml
│   │   │   │   │   ├── ic_call_answer_video_low.xml
│   │   │   │   │   ├── ic_call_decline.xml
│   │   │   │   │   ├── ic_call_decline_low.xml
│   │   │   │   │   ├── ic_launcher_background.xml
│   │   │   │   │   ├── ic_launcher_foreground.xml
│   │   │   │   │   ├── icon_background.xml
│   │   │   │   │   ├── notification_bg.xml
│   │   │   │   │   ├── notification_bg_low.xml
│   │   │   │   │   ├── notification_icon_background.xml
│   │   │   │   │   └── notification_tile_bg.xml
│   │   │   │   ├── drawable-hdpi-v4/
│   │   │   │   │   ├── notification_bg_low_normal.9.png
│   │   │   │   │   ├── notification_bg_low_pressed.9.png
│   │   │   │   │   ├── notification_bg_normal.9.png
│   │   │   │   │   ├── notification_bg_normal_pressed.9.png
│   │   │   │   │   ├── notification_oversize_large_icon_bg.png
│   │   │   │   │   └── notify_panel_notification_icon_bg.png
│   │   │   │   ├── drawable-mdpi-v4/
│   │   │   │   │   ├── notification_bg_low_normal.9.png
│   │   │   │   │   ├── notification_bg_low_pressed.9.png
│   │   │   │   │   ├── notification_bg_normal.9.png
│   │   │   │   │   ├── notification_bg_normal_pressed.9.png
│   │   │   │   │   └── notify_panel_notification_icon_bg.png
│   │   │   │   ├── drawable-v21/
│   │   │   │   │   └── notification_action_background.xml
│   │   │   │   ├── drawable-v23/
│   │   │   │   │   ├── compat_splash_screen.xml
│   │   │   │   │   └── compat_splash_screen_no_icon_background.xml
│   │   │   │   ├── drawable-xhdpi-v4/
│   │   │   │   │   ├── notification_bg_low_normal.9.png
│   │   │   │   │   ├── notification_bg_low_pressed.9.png
│   │   │   │   │   ├── notification_bg_normal.9.png
│   │   │   │   │   ├── notification_bg_normal_pressed.9.png
│   │   │   │   │   └── notify_panel_notification_icon_bg.png
│   │   │   │   ├── layout/
│   │   │   │   │   ├── custom_dialog.xml
│   │   │   │   │   ├── ime_base_split_test_activity.xml
│   │   │   │   │   ├── ime_secondary_split_test_activity.xml
│   │   │   │   │   ├── notification_template_part_chronometer.xml
│   │   │   │   │   ├── notification_template_part_time.xml
│   │   │   │   │   └── splash_screen_view.xml
│   │   │   │   ├── layout-v21/
│   │   │   │   │   ├── notification_action.xml
│   │   │   │   │   ├── notification_action_tombstone.xml
│   │   │   │   │   ├── notification_template_custom_big.xml
│   │   │   │   │   └── notification_template_icon_group.xml
│   │   │   │   ├── mipmap-anydpi-v4/
│   │   │   │   │   ├── ic_launcher.xml
│   │   │   │   │   └── ic_launcher_round.xml
│   │   │   │   ├── mipmap-hdpi-v4/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-mdpi-v4/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-xhdpi-v4/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-xxhdpi-v4/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-xxxhdpi-v4/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── values/
│   │   │   │   │   └── values.xml
│   │   │   │   ├── values-af/
│   │   │   │   │   └── values-af.xml
│   │   │   │   ├── values-am/
│   │   │   │   │   └── values-am.xml
│   │   │   │   ├── values-ar/
│   │   │   │   │   └── values-ar.xml
│   │   │   │   ├── values-as/
│   │   │   │   │   └── values-as.xml
│   │   │   │   ├── values-az/
│   │   │   │   │   └── values-az.xml
│   │   │   │   ├── values-b+sr+Latn/
│   │   │   │   │   └── values-b+sr+Latn.xml
│   │   │   │   ├── values-be/
│   │   │   │   │   └── values-be.xml
│   │   │   │   ├── values-bg/
│   │   │   │   │   └── values-bg.xml
│   │   │   │   ├── values-bn/
│   │   │   │   │   └── values-bn.xml
│   │   │   │   ├── values-bs/
│   │   │   │   │   └── values-bs.xml
│   │   │   │   ├── values-ca/
│   │   │   │   │   └── values-ca.xml
│   │   │   │   ├── values-cs/
│   │   │   │   │   └── values-cs.xml
│   │   │   │   ├── values-da/
│   │   │   │   │   └── values-da.xml
│   │   │   │   ├── values-de/
│   │   │   │   │   └── values-de.xml
│   │   │   │   ├── values-el/
│   │   │   │   │   └── values-el.xml
│   │   │   │   ├── values-en-rAU/
│   │   │   │   │   └── values-en-rAU.xml
│   │   │   │   ├── values-en-rCA/
│   │   │   │   │   └── values-en-rCA.xml
│   │   │   │   ├── values-en-rGB/
│   │   │   │   │   └── values-en-rGB.xml
│   │   │   │   ├── values-en-rIN/
│   │   │   │   │   └── values-en-rIN.xml
│   │   │   │   ├── values-en-rXC/
│   │   │   │   │   └── values-en-rXC.xml
│   │   │   │   ├── values-es/
│   │   │   │   │   └── values-es.xml
│   │   │   │   ├── values-es-rUS/
│   │   │   │   │   └── values-es-rUS.xml
│   │   │   │   ├── values-et/
│   │   │   │   │   └── values-et.xml
│   │   │   │   ├── values-eu/
│   │   │   │   │   └── values-eu.xml
│   │   │   │   ├── values-fa/
│   │   │   │   │   └── values-fa.xml
│   │   │   │   ├── values-fi/
│   │   │   │   │   └── values-fi.xml
│   │   │   │   ├── values-fr/
│   │   │   │   │   └── values-fr.xml
│   │   │   │   ├── values-fr-rCA/
│   │   │   │   │   └── values-fr-rCA.xml
│   │   │   │   ├── values-gl/
│   │   │   │   │   └── values-gl.xml
│   │   │   │   ├── values-gu/
│   │   │   │   │   └── values-gu.xml
│   │   │   │   ├── values-hi/
│   │   │   │   │   └── values-hi.xml
│   │   │   │   ├── values-hr/
│   │   │   │   │   └── values-hr.xml
│   │   │   │   ├── values-hu/
│   │   │   │   │   └── values-hu.xml
│   │   │   │   ├── values-hy/
│   │   │   │   │   └── values-hy.xml
│   │   │   │   ├── values-in/
│   │   │   │   │   └── values-in.xml
│   │   │   │   ├── values-is/
│   │   │   │   │   └── values-is.xml
│   │   │   │   ├── values-it/
│   │   │   │   │   └── values-it.xml
│   │   │   │   ├── values-iw/
│   │   │   │   │   └── values-iw.xml
│   │   │   │   ├── values-ja/
│   │   │   │   │   └── values-ja.xml
│   │   │   │   ├── values-ka/
│   │   │   │   │   └── values-ka.xml
│   │   │   │   ├── values-kk/
│   │   │   │   │   └── values-kk.xml
│   │   │   │   ├── values-km/
│   │   │   │   │   └── values-km.xml
│   │   │   │   ├── values-kn/
│   │   │   │   │   └── values-kn.xml
│   │   │   │   ├── values-ko/
│   │   │   │   │   └── values-ko.xml
│   │   │   │   ├── values-ky/
│   │   │   │   │   └── values-ky.xml
│   │   │   │   ├── values-lo/
│   │   │   │   │   └── values-lo.xml
│   │   │   │   ├── values-lt/
│   │   │   │   │   └── values-lt.xml
│   │   │   │   ├── values-lv/
│   │   │   │   │   └── values-lv.xml
│   │   │   │   ├── values-mk/
│   │   │   │   │   └── values-mk.xml
│   │   │   │   ├── values-ml/
│   │   │   │   │   └── values-ml.xml
│   │   │   │   ├── values-mn/
│   │   │   │   │   └── values-mn.xml
│   │   │   │   ├── values-mr/
│   │   │   │   │   └── values-mr.xml
│   │   │   │   ├── values-ms/
│   │   │   │   │   └── values-ms.xml
│   │   │   │   ├── values-my/
│   │   │   │   │   └── values-my.xml
│   │   │   │   ├── values-nb/
│   │   │   │   │   └── values-nb.xml
│   │   │   │   ├── values-ne/
│   │   │   │   │   └── values-ne.xml
│   │   │   │   ├── values-night-v33/
│   │   │   │   │   └── values-night-v33.xml
│   │   │   │   ├── values-night-v8/
│   │   │   │   │   └── values-night-v8.xml
│   │   │   │   ├── values-nl/
│   │   │   │   │   └── values-nl.xml
│   │   │   │   ├── values-or/
│   │   │   │   │   └── values-or.xml
│   │   │   │   ├── values-pa/
│   │   │   │   │   └── values-pa.xml
│   │   │   │   ├── values-pl/
│   │   │   │   │   └── values-pl.xml
│   │   │   │   ├── values-pt/
│   │   │   │   │   └── values-pt.xml
│   │   │   │   ├── values-pt-rBR/
│   │   │   │   │   └── values-pt-rBR.xml
│   │   │   │   ├── values-pt-rPT/
│   │   │   │   │   └── values-pt-rPT.xml
│   │   │   │   ├── values-ro/
│   │   │   │   │   └── values-ro.xml
│   │   │   │   ├── values-ru/
│   │   │   │   │   └── values-ru.xml
│   │   │   │   ├── values-si/
│   │   │   │   │   └── values-si.xml
│   │   │   │   ├── values-sk/
│   │   │   │   │   └── values-sk.xml
│   │   │   │   ├── values-sl/
│   │   │   │   │   └── values-sl.xml
│   │   │   │   ├── values-sq/
│   │   │   │   │   └── values-sq.xml
│   │   │   │   ├── values-sr/
│   │   │   │   │   └── values-sr.xml
│   │   │   │   ├── values-sv/
│   │   │   │   │   └── values-sv.xml
│   │   │   │   ├── values-sw/
│   │   │   │   │   └── values-sw.xml
│   │   │   │   ├── values-ta/
│   │   │   │   │   └── values-ta.xml
│   │   │   │   ├── values-te/
│   │   │   │   │   └── values-te.xml
│   │   │   │   ├── values-th/
│   │   │   │   │   └── values-th.xml
│   │   │   │   ├── values-tl/
│   │   │   │   │   └── values-tl.xml
│   │   │   │   ├── values-tr/
│   │   │   │   │   └── values-tr.xml
│   │   │   │   ├── values-uk/
│   │   │   │   │   └── values-uk.xml
│   │   │   │   ├── values-ur/
│   │   │   │   │   └── values-ur.xml
│   │   │   │   ├── values-uz/
│   │   │   │   │   └── values-uz.xml
│   │   │   │   ├── values-v21/
│   │   │   │   │   └── values-v21.xml
│   │   │   │   ├── values-v27/
│   │   │   │   │   └── values-v27.xml
│   │   │   │   ├── values-v29/
│   │   │   │   │   └── values-v29.xml
│   │   │   │   ├── values-v30/
│   │   │   │   │   └── values-v30.xml
│   │   │   │   ├── values-v31/
│   │   │   │   │   └── values-v31.xml
│   │   │   │   ├── values-v33/
│   │   │   │   │   └── values-v33.xml
│   │   │   │   ├── values-vi/
│   │   │   │   │   └── values-vi.xml
│   │   │   │   ├── values-watch-v20/
│   │   │   │   │   └── values-watch-v20.xml
│   │   │   │   ├── values-zh-rCN/
│   │   │   │   │   └── values-zh-rCN.xml
│   │   │   │   ├── values-zh-rHK/
│   │   │   │   │   └── values-zh-rHK.xml
│   │   │   │   ├── values-zh-rTW/
│   │   │   │   │   └── values-zh-rTW.xml
│   │   │   │   ├── values-zu/
│   │   │   │   │   └── values-zu.xml
│   │   │   │   └── xml/
│   │   │   │       ├── backup_rules.xml
│   │   │   │       └── data_extraction_rules.xml
│   │   │   ├── merged_art_profile/release/mergeReleaseArtProfile/
│   │   │   │   └── baseline-prof.txt
│   │   │   ├── merged_java_res/
│   │   │   │   ├── debug/mergeDebugJavaResource/
│   │   │   │   │   └── base.jar
│   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestJavaResource/
│   │   │   │   │   └── feature-app.jar
│   │   │   │   └── release/
│   │   │   │       ├── mergeReleaseJavaResource/
│   │   │   │       │   └── base.jar
│   │   │   │       └── minifyReleaseWithR8/
│   │   │   │           └── base.jar
│   │   │   ├── merged_jni_libs/
│   │   │   │   ├── debug/mergeDebugJniLibFolders/out/
│   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestJniLibFolders/out/
│   │   │   │   └── release/mergeReleaseJniLibFolders/out/
│   │   │   ├── merged_manifest/
│   │   │   │   ├── debug/processDebugMainManifest/
│   │   │   │   │   └── AndroidManifest.xml
│   │   │   │   └── release/processReleaseMainManifest/
│   │   │   │       └── AndroidManifest.xml
│   │   │   ├── merged_manifests/
│   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   └── output-metadata.json
│   │   │   │   └── release/processReleaseManifest/
│   │   │   │       ├── AndroidManifest.xml
│   │   │   │       └── output-metadata.json
│   │   │   ├── merged_native_libs/
│   │   │   │   ├── debug/mergeDebugNativeLibs/out/lib/
│   │   │   │   │   ├── arm64-v8a/
│   │   │   │   │   │   └── libandroidx.graphics.path.so
│   │   │   │   │   ├── armeabi-v7a/
│   │   │   │   │   │   └── libandroidx.graphics.path.so
│   │   │   │   │   ├── x86/
│   │   │   │   │   │   └── libandroidx.graphics.path.so
│   │   │   │   │   └── x86_64/
│   │   │   │   │       └── libandroidx.graphics.path.so
│   │   │   │   └── release/mergeReleaseNativeLibs/out/lib/
│   │   │   │       ├── arm64-v8a/
│   │   │   │       │   └── libandroidx.graphics.path.so
│   │   │   │       ├── armeabi-v7a/
│   │   │   │       │   └── libandroidx.graphics.path.so
│   │   │   │       ├── x86/
│   │   │   │       │   └── libandroidx.graphics.path.so
│   │   │   │       └── x86_64/
│   │   │   │           └── libandroidx.graphics.path.so
│   │   │   ├── merged_res/
│   │   │   │   ├── debug/mergeDebugResources/
│   │   │   │   │   ├── drawable_ic_launcher_background.xml.flat
│   │   │   │   │   ├── drawable_ic_launcher_foreground.xml.flat
│   │   │   │   │   ├── mipmap-anydpi_ic_launcher.xml.flat
│   │   │   │   │   ├── mipmap-anydpi_ic_launcher_round.xml.flat
│   │   │   │   │   ├── mipmap-hdpi_ic_launcher.webp.flat
│   │   │   │   │   ├── mipmap-hdpi_ic_launcher_round.webp.flat
│   │   │   │   │   ├── mipmap-mdpi_ic_launcher.webp.flat
│   │   │   │   │   ├── mipmap-mdpi_ic_launcher_round.webp.flat
│   │   │   │   │   ├── mipmap-xhdpi_ic_launcher.webp.flat
│   │   │   │   │   ├── mipmap-xhdpi_ic_launcher_round.webp.flat
│   │   │   │   │   ├── mipmap-xxhdpi_ic_launcher.webp.flat
│   │   │   │   │   ├── mipmap-xxhdpi_ic_launcher_round.webp.flat
│   │   │   │   │   ├── mipmap-xxxhdpi_ic_launcher.webp.flat
│   │   │   │   │   ├── mipmap-xxxhdpi_ic_launcher_round.webp.flat
│   │   │   │   │   ├── values-af_values-af.arsc.flat
│   │   │   │   │   ├── values-am_values-am.arsc.flat
│   │   │   │   │   ├── values-ar_values-ar.arsc.flat
│   │   │   │   │   ├── values-as_values-as.arsc.flat
│   │   │   │   │   ├── values-az_values-az.arsc.flat
│   │   │   │   │   ├── values-b+sr+Latn_values-b+sr+Latn.arsc.flat
│   │   │   │   │   ├── values-be_values-be.arsc.flat
│   │   │   │   │   ├── values-bg_values-bg.arsc.flat
│   │   │   │   │   ├── values-bn_values-bn.arsc.flat
│   │   │   │   │   ├── values-bs_values-bs.arsc.flat
│   │   │   │   │   ├── values-ca_values-ca.arsc.flat
│   │   │   │   │   ├── values-cs_values-cs.arsc.flat
│   │   │   │   │   ├── values-da_values-da.arsc.flat
│   │   │   │   │   ├── values-de_values-de.arsc.flat
│   │   │   │   │   ├── values-el_values-el.arsc.flat
│   │   │   │   │   ├── values-en-rAU_values-en-rAU.arsc.flat
│   │   │   │   │   ├── values-en-rCA_values-en-rCA.arsc.flat
│   │   │   │   │   ├── values-en-rGB_values-en-rGB.arsc.flat
│   │   │   │   │   ├── values-en-rIN_values-en-rIN.arsc.flat
│   │   │   │   │   ├── values-en-rXC_values-en-rXC.arsc.flat
│   │   │   │   │   ├── values-es-rUS_values-es-rUS.arsc.flat
│   │   │   │   │   ├── values-es_values-es.arsc.flat
│   │   │   │   │   ├── values-et_values-et.arsc.flat
│   │   │   │   │   ├── values-eu_values-eu.arsc.flat
│   │   │   │   │   ├── values-fa_values-fa.arsc.flat
│   │   │   │   │   ├── values-fi_values-fi.arsc.flat
│   │   │   │   │   ├── values-fr-rCA_values-fr-rCA.arsc.flat
│   │   │   │   │   ├── values-fr_values-fr.arsc.flat
│   │   │   │   │   ├── values-gl_values-gl.arsc.flat
│   │   │   │   │   ├── values-gu_values-gu.arsc.flat
│   │   │   │   │   ├── values-hi_values-hi.arsc.flat
│   │   │   │   │   ├── values-hr_values-hr.arsc.flat
│   │   │   │   │   ├── values-hu_values-hu.arsc.flat
│   │   │   │   │   ├── values-hy_values-hy.arsc.flat
│   │   │   │   │   ├── values-in_values-in.arsc.flat
│   │   │   │   │   ├── values-is_values-is.arsc.flat
│   │   │   │   │   ├── values-it_values-it.arsc.flat
│   │   │   │   │   ├── values-iw_values-iw.arsc.flat
│   │   │   │   │   ├── values-ja_values-ja.arsc.flat
│   │   │   │   │   ├── values-ka_values-ka.arsc.flat
│   │   │   │   │   ├── values-kk_values-kk.arsc.flat
│   │   │   │   │   ├── values-km_values-km.arsc.flat
│   │   │   │   │   ├── values-kn_values-kn.arsc.flat
│   │   │   │   │   ├── values-ko_values-ko.arsc.flat
│   │   │   │   │   ├── values-ky_values-ky.arsc.flat
│   │   │   │   │   ├── values-lo_values-lo.arsc.flat
│   │   │   │   │   ├── values-lt_values-lt.arsc.flat
│   │   │   │   │   ├── values-lv_values-lv.arsc.flat
│   │   │   │   │   ├── values-mk_values-mk.arsc.flat
│   │   │   │   │   ├── values-ml_values-ml.arsc.flat
│   │   │   │   │   ├── values-mn_values-mn.arsc.flat
│   │   │   │   │   ├── values-mr_values-mr.arsc.flat
│   │   │   │   │   ├── values-ms_values-ms.arsc.flat
│   │   │   │   │   ├── values-my_values-my.arsc.flat
│   │   │   │   │   ├── values-nb_values-nb.arsc.flat
│   │   │   │   │   ├── values-ne_values-ne.arsc.flat
│   │   │   │   │   ├── values-night-v33_values-night-v33.arsc.flat
│   │   │   │   │   ├── values-night-v8_values-night-v8.arsc.flat
│   │   │   │   │   ├── values-nl_values-nl.arsc.flat
│   │   │   │   │   ├── values-or_values-or.arsc.flat
│   │   │   │   │   ├── values-pa_values-pa.arsc.flat
│   │   │   │   │   ├── values-pl_values-pl.arsc.flat
│   │   │   │   │   ├── values-pt-rBR_values-pt-rBR.arsc.flat
│   │   │   │   │   ├── values-pt-rPT_values-pt-rPT.arsc.flat
│   │   │   │   │   ├── values-pt_values-pt.arsc.flat
│   │   │   │   │   ├── values-ro_values-ro.arsc.flat
│   │   │   │   │   ├── values-ru_values-ru.arsc.flat
│   │   │   │   │   ├── values-si_values-si.arsc.flat
│   │   │   │   │   ├── values-sk_values-sk.arsc.flat
│   │   │   │   │   ├── values-sl_values-sl.arsc.flat
│   │   │   │   │   ├── values-sq_values-sq.arsc.flat
│   │   │   │   │   ├── values-sr_values-sr.arsc.flat
│   │   │   │   │   ├── values-sv_values-sv.arsc.flat
│   │   │   │   │   ├── values-sw_values-sw.arsc.flat
│   │   │   │   │   ├── values-ta_values-ta.arsc.flat
│   │   │   │   │   ├── values-te_values-te.arsc.flat
│   │   │   │   │   ├── values-th_values-th.arsc.flat
│   │   │   │   │   ├── values-tl_values-tl.arsc.flat
│   │   │   │   │   ├── values-tr_values-tr.arsc.flat
│   │   │   │   │   ├── values-uk_values-uk.arsc.flat
│   │   │   │   │   ├── values-ur_values-ur.arsc.flat
│   │   │   │   │   ├── values-uz_values-uz.arsc.flat
│   │   │   │   │   ├── values-v21_values-v21.arsc.flat
│   │   │   │   │   ├── values-v27_values-v27.arsc.flat
│   │   │   │   │   ├── values-v29_values-v29.arsc.flat
│   │   │   │   │   ├── values-v30_values-v30.arsc.flat
│   │   │   │   │   ├── values-v31_values-v31.arsc.flat
│   │   │   │   │   ├── values-v33_values-v33.arsc.flat
│   │   │   │   │   ├── values-vi_values-vi.arsc.flat
│   │   │   │   │   ├── values-watch-v20_values-watch-v20.arsc.flat
│   │   │   │   │   ├── values-zh-rCN_values-zh-rCN.arsc.flat
│   │   │   │   │   ├── values-zh-rHK_values-zh-rHK.arsc.flat
│   │   │   │   │   ├── values-zh-rTW_values-zh-rTW.arsc.flat
│   │   │   │   │   ├── values-zu_values-zu.arsc.flat
│   │   │   │   │   ├── values_values.arsc.flat
│   │   │   │   │   ├── xml_backup_rules.xml.flat
│   │   │   │   │   └── xml_data_extraction_rules.xml.flat
│   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestResources/
│   │   │   │   │   ├── values-af_values-af.arsc.flat
│   │   │   │   │   ├── values-am_values-am.arsc.flat
│   │   │   │   │   ├── values-ar_values-ar.arsc.flat
│   │   │   │   │   ├── values-as_values-as.arsc.flat
│   │   │   │   │   ├── values-az_values-az.arsc.flat
│   │   │   │   │   ├── values-b+sr+Latn_values-b+sr+Latn.arsc.flat
│   │   │   │   │   ├── values-be_values-be.arsc.flat
│   │   │   │   │   ├── values-bg_values-bg.arsc.flat
│   │   │   │   │   ├── values-bn_values-bn.arsc.flat
│   │   │   │   │   ├── values-bs_values-bs.arsc.flat
│   │   │   │   │   ├── values-ca_values-ca.arsc.flat
│   │   │   │   │   ├── values-cs_values-cs.arsc.flat
│   │   │   │   │   ├── values-da_values-da.arsc.flat
│   │   │   │   │   ├── values-de_values-de.arsc.flat
│   │   │   │   │   ├── values-el_values-el.arsc.flat
│   │   │   │   │   ├── values-en-rAU_values-en-rAU.arsc.flat
│   │   │   │   │   ├── values-en-rCA_values-en-rCA.arsc.flat
│   │   │   │   │   ├── values-en-rGB_values-en-rGB.arsc.flat
│   │   │   │   │   ├── values-en-rIN_values-en-rIN.arsc.flat
│   │   │   │   │   ├── values-en-rXC_values-en-rXC.arsc.flat
│   │   │   │   │   ├── values-es-rUS_values-es-rUS.arsc.flat
│   │   │   │   │   ├── values-es_values-es.arsc.flat
│   │   │   │   │   ├── values-et_values-et.arsc.flat
│   │   │   │   │   ├── values-eu_values-eu.arsc.flat
│   │   │   │   │   ├── values-fa_values-fa.arsc.flat
│   │   │   │   │   ├── values-fi_values-fi.arsc.flat
│   │   │   │   │   ├── values-fr-rCA_values-fr-rCA.arsc.flat
│   │   │   │   │   ├── values-fr_values-fr.arsc.flat
│   │   │   │   │   ├── values-gl_values-gl.arsc.flat
│   │   │   │   │   ├── values-gu_values-gu.arsc.flat
│   │   │   │   │   ├── values-hi_values-hi.arsc.flat
│   │   │   │   │   ├── values-hr_values-hr.arsc.flat
│   │   │   │   │   ├── values-hu_values-hu.arsc.flat
│   │   │   │   │   ├── values-hy_values-hy.arsc.flat
│   │   │   │   │   ├── values-in_values-in.arsc.flat
│   │   │   │   │   ├── values-is_values-is.arsc.flat
│   │   │   │   │   ├── values-it_values-it.arsc.flat
│   │   │   │   │   ├── values-iw_values-iw.arsc.flat
│   │   │   │   │   ├── values-ja_values-ja.arsc.flat
│   │   │   │   │   ├── values-ka_values-ka.arsc.flat
│   │   │   │   │   ├── values-kk_values-kk.arsc.flat
│   │   │   │   │   ├── values-km_values-km.arsc.flat
│   │   │   │   │   ├── values-kn_values-kn.arsc.flat
│   │   │   │   │   ├── values-ko_values-ko.arsc.flat
│   │   │   │   │   ├── values-ky_values-ky.arsc.flat
│   │   │   │   │   ├── values-lo_values-lo.arsc.flat
│   │   │   │   │   ├── values-lt_values-lt.arsc.flat
│   │   │   │   │   ├── values-lv_values-lv.arsc.flat
│   │   │   │   │   ├── values-mk_values-mk.arsc.flat
│   │   │   │   │   ├── values-ml_values-ml.arsc.flat
│   │   │   │   │   ├── values-mn_values-mn.arsc.flat
│   │   │   │   │   ├── values-mr_values-mr.arsc.flat
│   │   │   │   │   ├── values-ms_values-ms.arsc.flat
│   │   │   │   │   ├── values-my_values-my.arsc.flat
│   │   │   │   │   ├── values-nb_values-nb.arsc.flat
│   │   │   │   │   ├── values-ne_values-ne.arsc.flat
│   │   │   │   │   ├── values-nl_values-nl.arsc.flat
│   │   │   │   │   ├── values-or_values-or.arsc.flat
│   │   │   │   │   ├── values-pa_values-pa.arsc.flat
│   │   │   │   │   ├── values-pl_values-pl.arsc.flat
│   │   │   │   │   ├── values-pt-rBR_values-pt-rBR.arsc.flat
│   │   │   │   │   ├── values-pt-rPT_values-pt-rPT.arsc.flat
│   │   │   │   │   ├── values-pt_values-pt.arsc.flat
│   │   │   │   │   ├── values-ro_values-ro.arsc.flat
│   │   │   │   │   ├── values-ru_values-ru.arsc.flat
│   │   │   │   │   ├── values-si_values-si.arsc.flat
│   │   │   │   │   ├── values-sk_values-sk.arsc.flat
│   │   │   │   │   ├── values-sl_values-sl.arsc.flat
│   │   │   │   │   ├── values-sq_values-sq.arsc.flat
│   │   │   │   │   ├── values-sr_values-sr.arsc.flat
│   │   │   │   │   ├── values-sv_values-sv.arsc.flat
│   │   │   │   │   ├── values-sw_values-sw.arsc.flat
│   │   │   │   │   ├── values-ta_values-ta.arsc.flat
│   │   │   │   │   ├── values-te_values-te.arsc.flat
│   │   │   │   │   ├── values-th_values-th.arsc.flat
│   │   │   │   │   ├── values-tl_values-tl.arsc.flat
│   │   │   │   │   ├── values-tr_values-tr.arsc.flat
│   │   │   │   │   ├── values-uk_values-uk.arsc.flat
│   │   │   │   │   ├── values-ur_values-ur.arsc.flat
│   │   │   │   │   ├── values-uz_values-uz.arsc.flat
│   │   │   │   │   ├── values-v18_values-v18.arsc.flat
│   │   │   │   │   ├── values-v21_values-v21.arsc.flat
│   │   │   │   │   ├── values-v28_values-v28.arsc.flat
│   │   │   │   │   ├── values-vi_values-vi.arsc.flat
│   │   │   │   │   ├── values-zh-rCN_values-zh-rCN.arsc.flat
│   │   │   │   │   ├── values-zh-rHK_values-zh-rHK.arsc.flat
│   │   │   │   │   ├── values-zh-rTW_values-zh-rTW.arsc.flat
│   │   │   │   │   ├── values-zu_values-zu.arsc.flat
│   │   │   │   │   └── values_values.arsc.flat
│   │   │   │   └── release/mergeReleaseResources/
│   │   │   │       ├── anim-v21_fragment_fast_out_extra_slow_in.xml.flat
│   │   │   │       ├── animator_fragment_close_enter.xml.flat
│   │   │   │       ├── animator_fragment_close_exit.xml.flat
│   │   │   │       ├── animator_fragment_fade_enter.xml.flat
│   │   │   │       ├── animator_fragment_fade_exit.xml.flat
│   │   │   │       ├── animator_fragment_open_enter.xml.flat
│   │   │   │       ├── animator_fragment_open_exit.xml.flat
│   │   │   │       ├── color_vector_tint_color.xml.flat
│   │   │   │       ├── color_vector_tint_theme_color.xml.flat
│   │   │   │       ├── drawable-hdpi-v4_notification_bg_low_normal.9.png.flat
│   │   │   │       ├── drawable-hdpi-v4_notification_bg_low_pressed.9.png.flat
│   │   │   │       ├── drawable-hdpi-v4_notification_bg_normal.9.png.flat
│   │   │   │       ├── drawable-hdpi-v4_notification_bg_normal_pressed.9.png.flat
│   │   │   │       ├── drawable-hdpi-v4_notification_oversize_large_icon_bg.png.flat
│   │   │   │       ├── drawable-hdpi-v4_notify_panel_notification_icon_bg.png.flat
│   │   │   │       ├── drawable-mdpi-v4_notification_bg_low_normal.9.png.flat
│   │   │   │       ├── drawable-mdpi-v4_notification_bg_low_pressed.9.png.flat
│   │   │   │       ├── drawable-mdpi-v4_notification_bg_normal.9.png.flat
│   │   │   │       ├── drawable-mdpi-v4_notification_bg_normal_pressed.9.png.flat
│   │   │   │       ├── drawable-mdpi-v4_notify_panel_notification_icon_bg.png.flat
│   │   │   │       ├── drawable-v21_notification_action_background.xml.flat
│   │   │   │       ├── drawable-v23_compat_splash_screen.xml.flat
│   │   │   │       ├── drawable-v23_compat_splash_screen_no_icon_background.xml.flat
│   │   │   │       ├── drawable-xhdpi-v4_notification_bg_low_normal.9.png.flat
│   │   │   │       ├── drawable-xhdpi-v4_notification_bg_low_pressed.9.png.flat
│   │   │   │       ├── drawable-xhdpi-v4_notification_bg_normal.9.png.flat
│   │   │   │       ├── drawable-xhdpi-v4_notification_bg_normal_pressed.9.png.flat
│   │   │   │       ├── drawable-xhdpi-v4_notify_panel_notification_icon_bg.png.flat
│   │   │   │       ├── drawable_abc_vector_test.xml.flat
│   │   │   │       ├── drawable_ic_call_answer.xml.flat
│   │   │   │       ├── drawable_ic_call_answer_low.xml.flat
│   │   │   │       ├── drawable_ic_call_answer_video.xml.flat
│   │   │   │       ├── drawable_ic_call_answer_video_low.xml.flat
│   │   │   │       ├── drawable_ic_call_decline.xml.flat
│   │   │   │       ├── drawable_ic_call_decline_low.xml.flat
│   │   │   │       ├── drawable_ic_launcher_background.xml.flat
│   │   │   │       ├── drawable_ic_launcher_foreground.xml.flat
│   │   │   │       ├── drawable_icon_background.xml.flat
│   │   │   │       ├── drawable_notification_bg.xml.flat
│   │   │   │       ├── drawable_notification_bg_low.xml.flat
│   │   │   │       ├── drawable_notification_icon_background.xml.flat
│   │   │   │       ├── drawable_notification_tile_bg.xml.flat
│   │   │   │       ├── layout-v21_notification_action.xml.flat
│   │   │   │       ├── layout-v21_notification_action_tombstone.xml.flat
│   │   │   │       ├── layout-v21_notification_template_custom_big.xml.flat
│   │   │   │       ├── layout-v21_notification_template_icon_group.xml.flat
│   │   │   │       ├── layout_custom_dialog.xml.flat
│   │   │   │       ├── layout_ime_base_split_test_activity.xml.flat
│   │   │   │       ├── layout_ime_secondary_split_test_activity.xml.flat
│   │   │   │       ├── layout_notification_template_part_chronometer.xml.flat
│   │   │   │       ├── layout_notification_template_part_time.xml.flat
│   │   │   │       ├── layout_splash_screen_view.xml.flat
│   │   │   │       ├── mipmap-anydpi_ic_launcher.xml.flat
│   │   │   │       ├── mipmap-anydpi_ic_launcher_round.xml.flat
│   │   │   │       ├── mipmap-hdpi_ic_launcher.webp.flat
│   │   │   │       ├── mipmap-hdpi_ic_launcher_round.webp.flat
│   │   │   │       ├── mipmap-mdpi_ic_launcher.webp.flat
│   │   │   │       ├── mipmap-mdpi_ic_launcher_round.webp.flat
│   │   │   │       ├── mipmap-xhdpi_ic_launcher.webp.flat
│   │   │   │       ├── mipmap-xhdpi_ic_launcher_round.webp.flat
│   │   │   │       ├── mipmap-xxhdpi_ic_launcher.webp.flat
│   │   │   │       ├── mipmap-xxhdpi_ic_launcher_round.webp.flat
│   │   │   │       ├── mipmap-xxxhdpi_ic_launcher.webp.flat
│   │   │   │       ├── mipmap-xxxhdpi_ic_launcher_round.webp.flat
│   │   │   │       ├── values-af_values-af.arsc.flat
│   │   │   │       ├── values-am_values-am.arsc.flat
│   │   │   │       ├── values-ar_values-ar.arsc.flat
│   │   │   │       ├── values-as_values-as.arsc.flat
│   │   │   │       ├── values-az_values-az.arsc.flat
│   │   │   │       ├── values-b+sr+Latn_values-b+sr+Latn.arsc.flat
│   │   │   │       ├── values-be_values-be.arsc.flat
│   │   │   │       ├── values-bg_values-bg.arsc.flat
│   │   │   │       ├── values-bn_values-bn.arsc.flat
│   │   │   │       ├── values-bs_values-bs.arsc.flat
│   │   │   │       ├── values-ca_values-ca.arsc.flat
│   │   │   │       ├── values-cs_values-cs.arsc.flat
│   │   │   │       ├── values-da_values-da.arsc.flat
│   │   │   │       ├── values-de_values-de.arsc.flat
│   │   │   │       ├── values-el_values-el.arsc.flat
│   │   │   │       ├── values-en-rAU_values-en-rAU.arsc.flat
│   │   │   │       ├── values-en-rCA_values-en-rCA.arsc.flat
│   │   │   │       ├── values-en-rGB_values-en-rGB.arsc.flat
│   │   │   │       ├── values-en-rIN_values-en-rIN.arsc.flat
│   │   │   │       ├── values-en-rXC_values-en-rXC.arsc.flat
│   │   │   │       ├── values-es-rUS_values-es-rUS.arsc.flat
│   │   │   │       ├── values-es_values-es.arsc.flat
│   │   │   │       ├── values-et_values-et.arsc.flat
│   │   │   │       ├── values-eu_values-eu.arsc.flat
│   │   │   │       ├── values-fa_values-fa.arsc.flat
│   │   │   │       ├── values-fi_values-fi.arsc.flat
│   │   │   │       ├── values-fr-rCA_values-fr-rCA.arsc.flat
│   │   │   │       ├── values-fr_values-fr.arsc.flat
│   │   │   │       ├── values-gl_values-gl.arsc.flat
│   │   │   │       ├── values-gu_values-gu.arsc.flat
│   │   │   │       ├── values-hi_values-hi.arsc.flat
│   │   │   │       ├── values-hr_values-hr.arsc.flat
│   │   │   │       ├── values-hu_values-hu.arsc.flat
│   │   │   │       ├── values-hy_values-hy.arsc.flat
│   │   │   │       ├── values-in_values-in.arsc.flat
│   │   │   │       ├── values-is_values-is.arsc.flat
│   │   │   │       ├── values-it_values-it.arsc.flat
│   │   │   │       ├── values-iw_values-iw.arsc.flat
│   │   │   │       ├── values-ja_values-ja.arsc.flat
│   │   │   │       ├── values-ka_values-ka.arsc.flat
│   │   │   │       ├── values-kk_values-kk.arsc.flat
│   │   │   │       ├── values-km_values-km.arsc.flat
│   │   │   │       ├── values-kn_values-kn.arsc.flat
│   │   │   │       ├── values-ko_values-ko.arsc.flat
│   │   │   │       ├── values-ky_values-ky.arsc.flat
│   │   │   │       ├── values-lo_values-lo.arsc.flat
│   │   │   │       ├── values-lt_values-lt.arsc.flat
│   │   │   │       ├── values-lv_values-lv.arsc.flat
│   │   │   │       ├── values-mk_values-mk.arsc.flat
│   │   │   │       ├── values-ml_values-ml.arsc.flat
│   │   │   │       ├── values-mn_values-mn.arsc.flat
│   │   │   │       ├── values-mr_values-mr.arsc.flat
│   │   │   │       ├── values-ms_values-ms.arsc.flat
│   │   │   │       ├── values-my_values-my.arsc.flat
│   │   │   │       ├── values-nb_values-nb.arsc.flat
│   │   │   │       ├── values-ne_values-ne.arsc.flat
│   │   │   │       ├── values-night-v33_values-night-v33.arsc.flat
│   │   │   │       ├── values-night-v8_values-night-v8.arsc.flat
│   │   │   │       ├── values-nl_values-nl.arsc.flat
│   │   │   │       ├── values-or_values-or.arsc.flat
│   │   │   │       ├── values-pa_values-pa.arsc.flat
│   │   │   │       ├── values-pl_values-pl.arsc.flat
│   │   │   │       ├── values-pt-rBR_values-pt-rBR.arsc.flat
│   │   │   │       ├── values-pt-rPT_values-pt-rPT.arsc.flat
│   │   │   │       ├── values-pt_values-pt.arsc.flat
│   │   │   │       ├── values-ro_values-ro.arsc.flat
│   │   │   │       ├── values-ru_values-ru.arsc.flat
│   │   │   │       ├── values-si_values-si.arsc.flat
│   │   │   │       ├── values-sk_values-sk.arsc.flat
│   │   │   │       ├── values-sl_values-sl.arsc.flat
│   │   │   │       ├── values-sq_values-sq.arsc.flat
│   │   │   │       ├── values-sr_values-sr.arsc.flat
│   │   │   │       ├── values-sv_values-sv.arsc.flat
│   │   │   │       ├── values-sw_values-sw.arsc.flat
│   │   │   │       ├── values-ta_values-ta.arsc.flat
│   │   │   │       ├── values-te_values-te.arsc.flat
│   │   │   │       ├── values-th_values-th.arsc.flat
│   │   │   │       ├── values-tl_values-tl.arsc.flat
│   │   │   │       ├── values-tr_values-tr.arsc.flat
│   │   │   │       ├── values-uk_values-uk.arsc.flat
│   │   │   │       ├── values-ur_values-ur.arsc.flat
│   │   │   │       ├── values-uz_values-uz.arsc.flat
│   │   │   │       ├── values-v21_values-v21.arsc.flat
│   │   │   │       ├── values-v27_values-v27.arsc.flat
│   │   │   │       ├── values-v29_values-v29.arsc.flat
│   │   │   │       ├── values-v30_values-v30.arsc.flat
│   │   │   │       ├── values-v31_values-v31.arsc.flat
│   │   │   │       ├── values-v33_values-v33.arsc.flat
│   │   │   │       ├── values-vi_values-vi.arsc.flat
│   │   │   │       ├── values-watch-v20_values-watch-v20.arsc.flat
│   │   │   │       ├── values-zh-rCN_values-zh-rCN.arsc.flat
│   │   │   │       ├── values-zh-rHK_values-zh-rHK.arsc.flat
│   │   │   │       ├── values-zh-rTW_values-zh-rTW.arsc.flat
│   │   │   │       ├── values-zu_values-zu.arsc.flat
│   │   │   │       ├── values_values.arsc.flat
│   │   │   │       ├── xml_backup_rules.xml.flat
│   │   │   │       └── xml_data_extraction_rules.xml.flat
│   │   │   ├── merged_res_blame_folder/
│   │   │   │   ├── debug/mergeDebugResources/out/
│   │   │   │   │   ├── multi-v2/
│   │   │   │   │   │   ├── mergeDebugResources.json
│   │   │   │   │   │   ├── values-af.json
│   │   │   │   │   │   ├── values-am.json
│   │   │   │   │   │   ├── values-ar.json
│   │   │   │   │   │   ├── values-as.json
│   │   │   │   │   │   ├── values-az.json
│   │   │   │   │   │   ├── values-b+sr+Latn.json
│   │   │   │   │   │   ├── values-be.json
│   │   │   │   │   │   ├── values-bg.json
│   │   │   │   │   │   ├── values-bn.json
│   │   │   │   │   │   ├── values-bs.json
│   │   │   │   │   │   ├── values-ca.json
│   │   │   │   │   │   ├── values-cs.json
│   │   │   │   │   │   ├── values-da.json
│   │   │   │   │   │   ├── values-de.json
│   │   │   │   │   │   ├── values-el.json
│   │   │   │   │   │   ├── values-en-rAU.json
│   │   │   │   │   │   ├── values-en-rCA.json
│   │   │   │   │   │   ├── values-en-rGB.json
│   │   │   │   │   │   ├── values-en-rIN.json
│   │   │   │   │   │   ├── values-en-rXC.json
│   │   │   │   │   │   ├── values-es-rUS.json
│   │   │   │   │   │   ├── values-es.json
│   │   │   │   │   │   ├── values-et.json
│   │   │   │   │   │   ├── values-eu.json
│   │   │   │   │   │   ├── values-fa.json
│   │   │   │   │   │   ├── values-fi.json
│   │   │   │   │   │   ├── values-fr-rCA.json
│   │   │   │   │   │   ├── values-fr.json
│   │   │   │   │   │   ├── values-gl.json
│   │   │   │   │   │   ├── values-gu.json
│   │   │   │   │   │   ├── values-hi.json
│   │   │   │   │   │   ├── values-hr.json
│   │   │   │   │   │   ├── values-hu.json
│   │   │   │   │   │   ├── values-hy.json
│   │   │   │   │   │   ├── values-in.json
│   │   │   │   │   │   ├── values-is.json
│   │   │   │   │   │   ├── values-it.json
│   │   │   │   │   │   ├── values-iw.json
│   │   │   │   │   │   ├── values-ja.json
│   │   │   │   │   │   ├── values-ka.json
│   │   │   │   │   │   ├── values-kk.json
│   │   │   │   │   │   ├── values-km.json
│   │   │   │   │   │   ├── values-kn.json
│   │   │   │   │   │   ├── values-ko.json
│   │   │   │   │   │   ├── values-ky.json
│   │   │   │   │   │   ├── values-lo.json
│   │   │   │   │   │   ├── values-lt.json
│   │   │   │   │   │   ├── values-lv.json
│   │   │   │   │   │   ├── values-mk.json
│   │   │   │   │   │   ├── values-ml.json
│   │   │   │   │   │   ├── values-mn.json
│   │   │   │   │   │   ├── values-mr.json
│   │   │   │   │   │   ├── values-ms.json
│   │   │   │   │   │   ├── values-my.json
│   │   │   │   │   │   ├── values-nb.json
│   │   │   │   │   │   ├── values-ne.json
│   │   │   │   │   │   ├── values-night-v33.json
│   │   │   │   │   │   ├── values-night-v8.json
│   │   │   │   │   │   ├── values-nl.json
│   │   │   │   │   │   ├── values-or.json
│   │   │   │   │   │   ├── values-pa.json
│   │   │   │   │   │   ├── values-pl.json
│   │   │   │   │   │   ├── values-pt-rBR.json
│   │   │   │   │   │   ├── values-pt-rPT.json
│   │   │   │   │   │   ├── values-pt.json
│   │   │   │   │   │   ├── values-ro.json
│   │   │   │   │   │   ├── values-ru.json
│   │   │   │   │   │   ├── values-si.json
│   │   │   │   │   │   ├── values-sk.json
│   │   │   │   │   │   ├── values-sl.json
│   │   │   │   │   │   ├── values-sq.json
│   │   │   │   │   │   ├── values-sr.json
│   │   │   │   │   │   ├── values-sv.json
│   │   │   │   │   │   ├── values-sw.json
│   │   │   │   │   │   ├── values-ta.json
│   │   │   │   │   │   ├── values-te.json
│   │   │   │   │   │   ├── values-th.json
│   │   │   │   │   │   ├── values-tl.json
│   │   │   │   │   │   ├── values-tr.json
│   │   │   │   │   │   ├── values-uk.json
│   │   │   │   │   │   ├── values-ur.json
│   │   │   │   │   │   ├── values-uz.json
│   │   │   │   │   │   ├── values-v21.json
│   │   │   │   │   │   ├── values-v27.json
│   │   │   │   │   │   ├── values-v29.json
│   │   │   │   │   │   ├── values-v30.json
│   │   │   │   │   │   ├── values-v31.json
│   │   │   │   │   │   ├── values-v33.json
│   │   │   │   │   │   ├── values-vi.json
│   │   │   │   │   │   ├── values-watch-v20.json
│   │   │   │   │   │   ├── values-zh-rCN.json
│   │   │   │   │   │   ├── values-zh-rHK.json
│   │   │   │   │   │   ├── values-zh-rTW.json
│   │   │   │   │   │   ├── values-zu.json
│   │   │   │   │   │   └── values.json
│   │   │   │   │   └── single/
│   │   │   │   │       └── mergeDebugResources.json
│   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestResources/out/multi-v2/
│   │   │   │   │   ├── mergeDebugAndroidTestResources.json
│   │   │   │   │   ├── values-af.json
│   │   │   │   │   ├── values-am.json
│   │   │   │   │   ├── values-ar.json
│   │   │   │   │   ├── values-as.json
│   │   │   │   │   ├── values-az.json
│   │   │   │   │   ├── values-b+sr+Latn.json
│   │   │   │   │   ├── values-be.json
│   │   │   │   │   ├── values-bg.json
│   │   │   │   │   ├── values-bn.json
│   │   │   │   │   ├── values-bs.json
│   │   │   │   │   ├── values-ca.json
│   │   │   │   │   ├── values-cs.json
│   │   │   │   │   ├── values-da.json
│   │   │   │   │   ├── values-de.json
│   │   │   │   │   ├── values-el.json
│   │   │   │   │   ├── values-en-rAU.json
│   │   │   │   │   ├── values-en-rCA.json
│   │   │   │   │   ├── values-en-rGB.json
│   │   │   │   │   ├── values-en-rIN.json
│   │   │   │   │   ├── values-en-rXC.json
│   │   │   │   │   ├── values-es-rUS.json
│   │   │   │   │   ├── values-es.json
│   │   │   │   │   ├── values-et.json
│   │   │   │   │   ├── values-eu.json
│   │   │   │   │   ├── values-fa.json
│   │   │   │   │   ├── values-fi.json
│   │   │   │   │   ├── values-fr-rCA.json
│   │   │   │   │   ├── values-fr.json
│   │   │   │   │   ├── values-gl.json
│   │   │   │   │   ├── values-gu.json
│   │   │   │   │   ├── values-hi.json
│   │   │   │   │   ├── values-hr.json
│   │   │   │   │   ├── values-hu.json
│   │   │   │   │   ├── values-hy.json
│   │   │   │   │   ├── values-in.json
│   │   │   │   │   ├── values-is.json
│   │   │   │   │   ├── values-it.json
│   │   │   │   │   ├── values-iw.json
│   │   │   │   │   ├── values-ja.json
│   │   │   │   │   ├── values-ka.json
│   │   │   │   │   ├── values-kk.json
│   │   │   │   │   ├── values-km.json
│   │   │   │   │   ├── values-kn.json
│   │   │   │   │   ├── values-ko.json
│   │   │   │   │   ├── values-ky.json
│   │   │   │   │   ├── values-lo.json
│   │   │   │   │   ├── values-lt.json
│   │   │   │   │   ├── values-lv.json
│   │   │   │   │   ├── values-mk.json
│   │   │   │   │   ├── values-ml.json
│   │   │   │   │   ├── values-mn.json
│   │   │   │   │   ├── values-mr.json
│   │   │   │   │   ├── values-ms.json
│   │   │   │   │   ├── values-my.json
│   │   │   │   │   ├── values-nb.json
│   │   │   │   │   ├── values-ne.json
│   │   │   │   │   ├── values-nl.json
│   │   │   │   │   ├── values-or.json
│   │   │   │   │   ├── values-pa.json
│   │   │   │   │   ├── values-pl.json
│   │   │   │   │   ├── values-pt-rBR.json
│   │   │   │   │   ├── values-pt-rPT.json
│   │   │   │   │   ├── values-pt.json
│   │   │   │   │   ├── values-ro.json
│   │   │   │   │   ├── values-ru.json
│   │   │   │   │   ├── values-si.json
│   │   │   │   │   ├── values-sk.json
│   │   │   │   │   ├── values-sl.json
│   │   │   │   │   ├── values-sq.json
│   │   │   │   │   ├── values-sr.json
│   │   │   │   │   ├── values-sv.json
│   │   │   │   │   ├── values-sw.json
│   │   │   │   │   ├── values-ta.json
│   │   │   │   │   ├── values-te.json
│   │   │   │   │   ├── values-th.json
│   │   │   │   │   ├── values-tl.json
│   │   │   │   │   ├── values-tr.json
│   │   │   │   │   ├── values-uk.json
│   │   │   │   │   ├── values-ur.json
│   │   │   │   │   ├── values-uz.json
│   │   │   │   │   ├── values-v18.json
│   │   │   │   │   ├── values-v21.json
│   │   │   │   │   ├── values-v28.json
│   │   │   │   │   ├── values-vi.json
│   │   │   │   │   ├── values-zh-rCN.json
│   │   │   │   │   ├── values-zh-rHK.json
│   │   │   │   │   ├── values-zh-rTW.json
│   │   │   │   │   ├── values-zu.json
│   │   │   │   │   └── values.json
│   │   │   │   └── release/mergeReleaseResources/out/
│   │   │   │       ├── multi-v2/
│   │   │   │       │   ├── mergeReleaseResources.json
│   │   │   │       │   ├── values-af.json
│   │   │   │       │   ├── values-am.json
│   │   │   │       │   ├── values-ar.json
│   │   │   │       │   ├── values-as.json
│   │   │   │       │   ├── values-az.json
│   │   │   │       │   ├── values-b+sr+Latn.json
│   │   │   │       │   ├── values-be.json
│   │   │   │       │   ├── values-bg.json
│   │   │   │       │   ├── values-bn.json
│   │   │   │       │   ├── values-bs.json
│   │   │   │       │   ├── values-ca.json
│   │   │   │       │   ├── values-cs.json
│   │   │   │       │   ├── values-da.json
│   │   │   │       │   ├── values-de.json
│   │   │   │       │   ├── values-el.json
│   │   │   │       │   ├── values-en-rAU.json
│   │   │   │       │   ├── values-en-rCA.json
│   │   │   │       │   ├── values-en-rGB.json
│   │   │   │       │   ├── values-en-rIN.json
│   │   │   │       │   ├── values-en-rXC.json
│   │   │   │       │   ├── values-es-rUS.json
│   │   │   │       │   ├── values-es.json
│   │   │   │       │   ├── values-et.json
│   │   │   │       │   ├── values-eu.json
│   │   │   │       │   ├── values-fa.json
│   │   │   │       │   ├── values-fi.json
│   │   │   │       │   ├── values-fr-rCA.json
│   │   │   │       │   ├── values-fr.json
│   │   │   │       │   ├── values-gl.json
│   │   │   │       │   ├── values-gu.json
│   │   │   │       │   ├── values-hi.json
│   │   │   │       │   ├── values-hr.json
│   │   │   │       │   ├── values-hu.json
│   │   │   │       │   ├── values-hy.json
│   │   │   │       │   ├── values-in.json
│   │   │   │       │   ├── values-is.json
│   │   │   │       │   ├── values-it.json
│   │   │   │       │   ├── values-iw.json
│   │   │   │       │   ├── values-ja.json
│   │   │   │       │   ├── values-ka.json
│   │   │   │       │   ├── values-kk.json
│   │   │   │       │   ├── values-km.json
│   │   │   │       │   ├── values-kn.json
│   │   │   │       │   ├── values-ko.json
│   │   │   │       │   ├── values-ky.json
│   │   │   │       │   ├── values-lo.json
│   │   │   │       │   ├── values-lt.json
│   │   │   │       │   ├── values-lv.json
│   │   │   │       │   ├── values-mk.json
│   │   │   │       │   ├── values-ml.json
│   │   │   │       │   ├── values-mn.json
│   │   │   │       │   ├── values-mr.json
│   │   │   │       │   ├── values-ms.json
│   │   │   │       │   ├── values-my.json
│   │   │   │       │   ├── values-nb.json
│   │   │   │       │   ├── values-ne.json
│   │   │   │       │   ├── values-night-v33.json
│   │   │   │       │   ├── values-night-v8.json
│   │   │   │       │   ├── values-nl.json
│   │   │   │       │   ├── values-or.json
│   │   │   │       │   ├── values-pa.json
│   │   │   │       │   ├── values-pl.json
│   │   │   │       │   ├── values-pt-rBR.json
│   │   │   │       │   ├── values-pt-rPT.json
│   │   │   │       │   ├── values-pt.json
│   │   │   │       │   ├── values-ro.json
│   │   │   │       │   ├── values-ru.json
│   │   │   │       │   ├── values-si.json
│   │   │   │       │   ├── values-sk.json
│   │   │   │       │   ├── values-sl.json
│   │   │   │       │   ├── values-sq.json
│   │   │   │       │   ├── values-sr.json
│   │   │   │       │   ├── values-sv.json
│   │   │   │       │   ├── values-sw.json
│   │   │   │       │   ├── values-ta.json
│   │   │   │       │   ├── values-te.json
│   │   │   │       │   ├── values-th.json
│   │   │   │       │   ├── values-tl.json
│   │   │   │       │   ├── values-tr.json
│   │   │   │       │   ├── values-uk.json
│   │   │   │       │   ├── values-ur.json
│   │   │   │       │   ├── values-uz.json
│   │   │   │       │   ├── values-v21.json
│   │   │   │       │   ├── values-v27.json
│   │   │   │       │   ├── values-v29.json
│   │   │   │       │   ├── values-v30.json
│   │   │   │       │   ├── values-v31.json
│   │   │   │       │   ├── values-v33.json
│   │   │   │       │   ├── values-vi.json
│   │   │   │       │   ├── values-watch-v20.json
│   │   │   │       │   ├── values-zh-rCN.json
│   │   │   │       │   ├── values-zh-rHK.json
│   │   │   │       │   ├── values-zh-rTW.json
│   │   │   │       │   ├── values-zu.json
│   │   │   │       │   └── values.json
│   │   │   │       └── single/
│   │   │   │           └── mergeReleaseResources.json
│   │   │   ├── merged_startup_profile/release/mergeReleaseStartupProfile/
│   │   │   ├── merged_test_only_native_libs/
│   │   │   │   ├── debug/mergeDebugNativeLibs/out/
│   │   │   │   └── release/mergeReleaseNativeLibs/out/
│   │   │   ├── metadata_library_dependencies_report/release/collectReleaseDependencies/
│   │   │   │   └── dependencies.pb
│   │   │   ├── mixed_scope_dex_archive/
│   │   │   │   ├── debug/dexBuilderDebug/out/
│   │   │   │   └── debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   ├── native_symbol_tables/release/extractReleaseNativeSymbolTables/out/
│   │   │   ├── navigation_json/
│   │   │   │   ├── debug/extractDeepLinksDebug/
│   │   │   │   │   └── navigation.json
│   │   │   │   └── release/extractDeepLinksRelease/
│   │   │   │       └── navigation.json
│   │   │   ├── nested_resources_validation_report/
│   │   │   │   ├── debug/generateDebugResources/
│   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestResources/
│   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   └── release/generateReleaseResources/
│   │   │   │       └── nestedResourcesValidationReport.txt
│   │   │   ├── optimized_processed_res/release/optimizeReleaseResources/
│   │   │   │   ├── output-metadata.json
│   │   │   │   └── resources-release-optimize.ap_
│   │   │   ├── packaged_manifests/
│   │   │   │   ├── debug/processDebugManifestForPackage/
│   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── debugAndroidTest/processDebugAndroidTestManifest/
│   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── debugUnitTest/processDebugUnitTestManifest/
│   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   └── output-metadata.json
│   │   │   │   └── release/processReleaseManifestForPackage/
│   │   │   │       ├── AndroidManifest.xml
│   │   │   │       └── output-metadata.json
│   │   │   ├── packaged_res/
│   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   ├── drawable/
│   │   │   │   │   │   ├── ic_launcher_background.xml
│   │   │   │   │   │   └── ic_launcher_foreground.xml
│   │   │   │   │   ├── mipmap-anydpi-v4/
│   │   │   │   │   │   ├── ic_launcher.xml
│   │   │   │   │   │   └── ic_launcher_round.xml
│   │   │   │   │   ├── mipmap-hdpi-v4/
│   │   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   │   ├── mipmap-mdpi-v4/
│   │   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   │   ├── mipmap-xhdpi-v4/
│   │   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   │   ├── mipmap-xxhdpi-v4/
│   │   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   │   ├── mipmap-xxxhdpi-v4/
│   │   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   │   ├── values/
│   │   │   │   │   │   └── values.xml
│   │   │   │   │   └── xml/
│   │   │   │   │       ├── backup_rules.xml
│   │   │   │   │       └── data_extraction_rules.xml
│   │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/
│   │   │   │   └── release/packageReleaseResources/
│   │   │   │       ├── drawable/
│   │   │   │       │   ├── ic_launcher_background.xml
│   │   │   │       │   └── ic_launcher_foreground.xml
│   │   │   │       ├── mipmap-anydpi-v4/
│   │   │   │       │   ├── ic_launcher.xml
│   │   │   │       │   └── ic_launcher_round.xml
│   │   │   │       ├── mipmap-hdpi-v4/
│   │   │   │       │   ├── ic_launcher.webp
│   │   │   │       │   └── ic_launcher_round.webp
│   │   │   │       ├── mipmap-mdpi-v4/
│   │   │   │       │   ├── ic_launcher.webp
│   │   │   │       │   └── ic_launcher_round.webp
│   │   │   │       ├── mipmap-xhdpi-v4/
│   │   │   │       │   ├── ic_launcher.webp
│   │   │   │       │   └── ic_launcher_round.webp
│   │   │   │       ├── mipmap-xxhdpi-v4/
│   │   │   │       │   ├── ic_launcher.webp
│   │   │   │       │   └── ic_launcher_round.webp
│   │   │   │       ├── mipmap-xxxhdpi-v4/
│   │   │   │       │   ├── ic_launcher.webp
│   │   │   │       │   └── ic_launcher_round.webp
│   │   │   │       ├── values/
│   │   │   │       │   └── values.xml
│   │   │   │       └── xml/
│   │   │   │           ├── backup_rules.xml
│   │   │   │           └── data_extraction_rules.xml
│   │   │   ├── project_dex_archive/
│   │   │   │   ├── debug/dexBuilderDebug/out/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/
│   │   │   │   │   │   ├── app/navigation/
│   │   │   │   │   │   │   ├── ComposableSingletons$NavigationWrapperKt.dex
│   │   │   │   │   │   │   ├── NavigationGatesEntryPoint.dex
│   │   │   │   │   │   │   ├── NavigationGatesKt$PostLoginGateRoute$1$1.dex
│   │   │   │   │   │   │   ├── NavigationGatesKt$SplashGateScreen$1$1.dex
│   │   │   │   │   │   │   ├── NavigationGatesKt.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$$inlined$entryProvider$default$1.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$1.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$10.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$11.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$12.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$13.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$2.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$3.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$4.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$5.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$6.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$7.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$8.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt$NavigationWrapper$lambda$4$$inlined$entry$default$9.dex
│   │   │   │   │   │   │   ├── NavigationWrapperKt.dex
│   │   │   │   │   │   │   ├── Routes$App.dex
│   │   │   │   │   │   │   ├── Routes$CreateVault.dex
│   │   │   │   │   │   │   ├── Routes$Error.dex
│   │   │   │   │   │   │   ├── Routes$Login.dex
│   │   │   │   │   │   │   ├── Routes$PostLoginGate.dex
│   │   │   │   │   │   │   ├── Routes$Profile.dex
│   │   │   │   │   │   │   ├── Routes$RecoveryKey.dex
│   │   │   │   │   │   │   ├── Routes$Settings.dex
│   │   │   │   │   │   │   ├── Routes$Signup.dex
│   │   │   │   │   │   │   ├── Routes$Splash.dex
│   │   │   │   │   │   │   ├── Routes$UnlockVault.dex
│   │   │   │   │   │   │   ├── Routes$Vault.dex
│   │   │   │   │   │   │   ├── Routes$VaultFolders.dex
│   │   │   │   │   │   │   ├── Routes$Welcome.dex
│   │   │   │   │   │   │   ├── Routes.dex
│   │   │   │   │   │   │   └── RoutesKt.dex
│   │   │   │   │   │   ├── ui/theme/
│   │   │   │   │   │   │   ├── ColorKt.dex
│   │   │   │   │   │   │   ├── ThemeKt.dex
│   │   │   │   │   │   │   └── TypeKt.dex
│   │   │   │   │   │   ├── ComposableSingletons$MainActivityKt.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityCBuilder.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityCImpl.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCBuilder.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCImpl$SwitchingProvider.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCImpl.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$Builder.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$FragmentCBuilder.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$FragmentCImpl.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ServiceCBuilder.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ServiceCImpl.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$SingletonCImpl$SwitchingProvider.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$SingletonCImpl.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewCBuilder.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewCImpl.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewModelCBuilder.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewModelCImpl.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewWithFragmentCBuilder.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewWithFragmentCImpl.dex
│   │   │   │   │   │   ├── DaggerSafeCubeApp_HiltComponents_SingletonC.dex
│   │   │   │   │   │   ├── Hilt_SafeCubeApp$1.dex
│   │   │   │   │   │   ├── Hilt_SafeCubeApp.dex
│   │   │   │   │   │   ├── MainActivity.dex
│   │   │   │   │   │   ├── SafeCubeApp.dex
│   │   │   │   │   │   ├── SafeCubeApp_ComponentTreeDeps.dex
│   │   │   │   │   │   ├── SafeCubeApp_GeneratedInjector.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ActivityC$Builder.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ActivityC.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ActivityCBuilderModule.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedC$Builder.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedC.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ActivityRetainedCBuilderModule.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$FragmentC$Builder.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$FragmentC.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$FragmentCBuilderModule.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ServiceC$Builder.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ServiceC.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ServiceCBuilderModule.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$SingletonC.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewC$Builder.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewC.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewCBuilderModule.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewModelC$Builder.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewModelC.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewModelCBuilderModule.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentC$Builder.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentC.dex
│   │   │   │   │   │   ├── SafeCubeApp_HiltComponents$ViewWithFragmentCBuilderModule.dex
│   │   │   │   │   │   └── SafeCubeApp_HiltComponents.dex
│   │   │   │   │   ├── dagger/hilt/internal/
│   │   │   │   │   │   ├── aggregatedroot/codegen/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.dex
│   │   │   │   │   │   └── processedrootsentinel/codegen/
│   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_SafeCubeApp.dex
│   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   ├── _com_miguelrodriguez19_safecube_app_navigation_NavigationGatesEntryPoint.dex
│   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.dex
│   │   │   │   │   ├── 98aad08d7d784ef9607104cdbdc250cbc8a27f2e3d33431a21c654efe2cd2ed8_0.jar
│   │   │   │   │   ├── 98aad08d7d784ef9607104cdbdc250cbc8a27f2e3d33431a21c654efe2cd2ed8_1.jar
│   │   │   │   │   ├── 98aad08d7d784ef9607104cdbdc250cbc8a27f2e3d33431a21c654efe2cd2ed8_2.jar
│   │   │   │   │   ├── 98aad08d7d784ef9607104cdbdc250cbc8a27f2e3d33431a21c654efe2cd2ed8_3.jar
│   │   │   │   │   ├── 98aad08d7d784ef9607104cdbdc250cbc8a27f2e3d33431a21c654efe2cd2ed8_4.jar
│   │   │   │   │   └── 98aad08d7d784ef9607104cdbdc250cbc8a27f2e3d33431a21c654efe2cd2ed8_5.jar
│   │   │   │   └── debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │       ├── com/miguelrodriguez19/safecube/
│   │   │   │       │   ├── ComposableSingletons$MainActivityComposeTestKt.dex
│   │   │   │       │   ├── ExampleInstrumentedTest.dex
│   │   │   │       │   └── MainActivityComposeTest.dex
│   │   │   │       ├── 0f049ea26bab4bb4d58fce2c44254fbc95e9c73bcf5e384134db5d7f11cc5c66_0.jar
│   │   │   │       ├── 0f049ea26bab4bb4d58fce2c44254fbc95e9c73bcf5e384134db5d7f11cc5c66_1.jar
│   │   │   │       ├── 0f049ea26bab4bb4d58fce2c44254fbc95e9c73bcf5e384134db5d7f11cc5c66_2.jar
│   │   │   │       ├── 0f049ea26bab4bb4d58fce2c44254fbc95e9c73bcf5e384134db5d7f11cc5c66_3.jar
│   │   │   │       └── 0f049ea26bab4bb4d58fce2c44254fbc95e9c73bcf5e384134db5d7f11cc5c66_5.jar
│   │   │   ├── r8_art_profile/release/
│   │   │   │   ├── expandReleaseArtProfileWildcards/
│   │   │   │   │   └── baseline-prof.txt
│   │   │   │   └── minifyReleaseWithR8/
│   │   │   │       └── baseline-prof.txt
│   │   │   ├── r8_metadata/release/minifyReleaseWithR8/
│   │   │   │   └── r8-metadata.dat
│   │   │   ├── runtime_app_classes_jar/debug/bundleDebugClassesToRuntimeJar/
│   │   │   │   └── classes.jar
│   │   │   ├── runtime_symbol_list/
│   │   │   │   ├── debug/processDebugResources/
│   │   │   │   │   └── R.txt
│   │   │   │   ├── debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── R.txt
│   │   │   │   └── release/processReleaseResources/
│   │   │   │       └── R.txt
│   │   │   ├── sdk_dependency_data/release/sdkReleaseDependencyData/
│   │   │   │   └── sdkDependencyData.pb
│   │   │   ├── shrunk_resources_binary_format/release/convertShrunkResourcesToBinaryRelease/
│   │   │   │   ├── output-metadata.json
│   │   │   │   └── shrunk-resources-binary-format-release.ap_
│   │   │   ├── shrunk_resources_proto_format/release/minifyReleaseWithR8/
│   │   │   │   ├── output-metadata.json
│   │   │   │   └── shrunk-resources-proto-format-release.ap_
│   │   │   ├── signing_config_versions/
│   │   │   │   ├── debug/writeDebugSigningConfigVersions/
│   │   │   │   │   └── signing-config-versions.json
│   │   │   │   ├── debugAndroidTest/writeDebugAndroidTestSigningConfigVersions/
│   │   │   │   │   └── signing-config-versions.json
│   │   │   │   └── release/writeReleaseSigningConfigVersions/
│   │   │   │       └── signing-config-versions.json
│   │   │   ├── stable_resource_ids_file/
│   │   │   │   ├── debug/processDebugResources/
│   │   │   │   │   └── stableIds.txt
│   │   │   │   ├── debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── stableIds.txt
│   │   │   │   └── release/processReleaseResources/
│   │   │   │       └── stableIds.txt
│   │   │   ├── stripped_native_libs/
│   │   │   │   ├── debug/stripDebugDebugSymbols/out/lib/
│   │   │   │   │   ├── arm64-v8a/
│   │   │   │   │   │   └── libandroidx.graphics.path.so
│   │   │   │   │   ├── armeabi-v7a/
│   │   │   │   │   │   └── libandroidx.graphics.path.so
│   │   │   │   │   ├── x86/
│   │   │   │   │   │   └── libandroidx.graphics.path.so
│   │   │   │   │   └── x86_64/
│   │   │   │   │       └── libandroidx.graphics.path.so
│   │   │   │   └── release/stripReleaseDebugSymbols/out/lib/
│   │   │   │       ├── arm64-v8a/
│   │   │   │       │   └── libandroidx.graphics.path.so
│   │   │   │       ├── armeabi-v7a/
│   │   │   │       │   └── libandroidx.graphics.path.so
│   │   │   │       ├── x86/
│   │   │   │       │   └── libandroidx.graphics.path.so
│   │   │   │       └── x86_64/
│   │   │   │           └── libandroidx.graphics.path.so
│   │   │   ├── sub_project_dex_archive/
│   │   │   │   ├── debug/dexBuilderDebug/out/
│   │   │   │   └── debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   ├── symbol_list_with_package_name/
│   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   └── package-aware-r.txt
│   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   └── package-aware-r.txt
│   │   │   │   └── release/generateReleaseRFile/
│   │   │   │       └── package-aware-r.txt
│   │   │   ├── tmp/manifest/
│   │   │   │   ├── androidTest/debug/
│   │   │   │   └── test/debug/
│   │   │   ├── unit_test_config_directory/debugUnitTest/generateDebugUnitTestConfig/out/com/android/tools/
│   │   │   │   │   │   │   ├── validate_signing_config/
│   │   │   │   ├── debug/validateSigningDebug/
│   │   │   │   ├── debugAndroidTest/validateSigningDebugAndroidTest/
│   │   │   │   └── release/validateSigningRelease/
│   │   │   └── version_control_info_file/release/extractReleaseVersionControlInfo/
│   │   │       └── version-control-info.textproto
│   │   ├── kotlin/
│   │   │   ├── compileDebugAndroidTestKotlin/
│   │   │   │   ├── cacheable/
│   │   │   │   │   ├── caches-jvm/
│   │   │   │   │   │   ├── compilerPluginFiles/
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │   │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │   │   │   ├── inputs/
│   │   │   │   │   │   │   ├── source-to-output.tab
│   │   │   │   │   │   │   ├── source-to-output.tab.keystream
│   │   │   │   │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │   │   │   │   ├── source-to-output.tab.len
│   │   │   │   │   │   │   ├── source-to-output.tab.values.at
│   │   │   │   │   │   │   ├── source-to-output.tab_i
│   │   │   │   │   │   │   └── source-to-output.tab_i.len
│   │   │   │   │   │   ├── jvm/
│   │   │   │   │   │   │   └── kotlin/
│   │   │   │   │   │   │       ├── class-attributes.tab
│   │   │   │   │   │   │       ├── class-attributes.tab.keystream
│   │   │   │   │   │   │       ├── class-attributes.tab.keystream.len
│   │   │   │   │   │   │       ├── class-attributes.tab.len
│   │   │   │   │   │   │       ├── class-attributes.tab.values.at
│   │   │   │   │   │   │       ├── class-attributes.tab_i
│   │   │   │   │   │   │       ├── class-attributes.tab_i.len
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.len
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.values.at
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i.len
│   │   │   │   │   │   │       ├── internal-name-to-source.tab
│   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │   │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │   │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │   │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │   │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │   │   │   │       ├── proto.tab
│   │   │   │   │   │   │       ├── proto.tab.keystream
│   │   │   │   │   │   │       ├── proto.tab.keystream.len
│   │   │   │   │   │   │       ├── proto.tab.len
│   │   │   │   │   │   │       ├── proto.tab.values.at
│   │   │   │   │   │   │       ├── proto.tab_i
│   │   │   │   │   │   │       ├── proto.tab_i.len
│   │   │   │   │   │   │       ├── source-to-classes.tab
│   │   │   │   │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │   │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │   │   │   │       ├── source-to-classes.tab.len
│   │   │   │   │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │   │   │   │       ├── source-to-classes.tab_i
│   │   │   │   │   │   │       └── source-to-classes.tab_i.len
│   │   │   │   │   │   └── lookups/
│   │   │   │   │   │       ├── counters.tab
│   │   │   │   │   │       ├── file-to-id.tab
│   │   │   │   │   │       ├── file-to-id.tab.keystream
│   │   │   │   │   │       ├── file-to-id.tab.keystream.len
│   │   │   │   │   │       ├── file-to-id.tab.len
│   │   │   │   │   │       ├── file-to-id.tab.values.at
│   │   │   │   │   │       ├── file-to-id.tab_i
│   │   │   │   │   │       ├── file-to-id.tab_i.len
│   │   │   │   │   │       ├── id-to-file.tab
│   │   │   │   │   │       ├── id-to-file.tab.keystream
│   │   │   │   │   │       ├── id-to-file.tab.keystream.len
│   │   │   │   │   │       ├── id-to-file.tab.len
│   │   │   │   │   │       ├── id-to-file.tab.values.at
│   │   │   │   │   │       ├── id-to-file.tab_i
│   │   │   │   │   │       ├── id-to-file.tab_i.len
│   │   │   │   │   │       ├── lookups.tab
│   │   │   │   │   │       ├── lookups.tab.keystream
│   │   │   │   │   │       ├── lookups.tab.keystream.len
│   │   │   │   │   │       ├── lookups.tab.len
│   │   │   │   │   │       ├── lookups.tab.values.at
│   │   │   │   │   │       ├── lookups.tab_i
│   │   │   │   │   │       └── lookups.tab_i.len
│   │   │   │   │   └── last-build.bin
│   │   │   │   ├── classpath-snapshot/
│   │   │   │   │   └── shrunk-classpath-snapshot.bin
│   │   │   │   └── local-state/
│   │   │   ├── compileDebugKotlin/
│   │   │   │   ├── cacheable/
│   │   │   │   │   ├── caches-jvm/
│   │   │   │   │   │   ├── compilerPluginFiles/
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │   │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │   │   │   ├── inputs/
│   │   │   │   │   │   │   ├── source-to-output.tab
│   │   │   │   │   │   │   ├── source-to-output.tab.keystream
│   │   │   │   │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │   │   │   │   ├── source-to-output.tab.len
│   │   │   │   │   │   │   ├── source-to-output.tab.values.at
│   │   │   │   │   │   │   ├── source-to-output.tab_i
│   │   │   │   │   │   │   └── source-to-output.tab_i.len
│   │   │   │   │   │   ├── jvm/
│   │   │   │   │   │   │   └── kotlin/
│   │   │   │   │   │   │       ├── class-attributes.tab
│   │   │   │   │   │   │       ├── class-attributes.tab.keystream
│   │   │   │   │   │   │       ├── class-attributes.tab.keystream.len
│   │   │   │   │   │   │       ├── class-attributes.tab.len
│   │   │   │   │   │   │       ├── class-attributes.tab.values.at
│   │   │   │   │   │   │       ├── class-attributes.tab_i
│   │   │   │   │   │   │       ├── class-attributes.tab_i.len
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.len
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.values.at
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i.len
│   │   │   │   │   │   │       ├── internal-name-to-source.tab
│   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │   │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │   │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │   │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │   │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │   │   │   │       ├── package-parts.tab
│   │   │   │   │   │   │       ├── package-parts.tab.keystream
│   │   │   │   │   │   │       ├── package-parts.tab.keystream.len
│   │   │   │   │   │   │       ├── package-parts.tab.len
│   │   │   │   │   │   │       ├── package-parts.tab.values.at
│   │   │   │   │   │   │       ├── package-parts.tab_i
│   │   │   │   │   │   │       ├── package-parts.tab_i.len
│   │   │   │   │   │   │       ├── proto.tab
│   │   │   │   │   │   │       ├── proto.tab.keystream
│   │   │   │   │   │   │       ├── proto.tab.keystream.len
│   │   │   │   │   │   │       ├── proto.tab.len
│   │   │   │   │   │   │       ├── proto.tab.values.at
│   │   │   │   │   │   │       ├── proto.tab_i
│   │   │   │   │   │   │       ├── proto.tab_i.len
│   │   │   │   │   │   │       ├── source-to-classes.tab
│   │   │   │   │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │   │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │   │   │   │       ├── source-to-classes.tab.len
│   │   │   │   │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │   │   │   │       ├── source-to-classes.tab_i
│   │   │   │   │   │   │       ├── source-to-classes.tab_i.len
│   │   │   │   │   │   │       ├── subtypes.tab
│   │   │   │   │   │   │       ├── subtypes.tab.keystream
│   │   │   │   │   │   │       ├── subtypes.tab.keystream.len
│   │   │   │   │   │   │       ├── subtypes.tab.len
│   │   │   │   │   │   │       ├── subtypes.tab.values.at
│   │   │   │   │   │   │       ├── subtypes.tab_i
│   │   │   │   │   │   │       ├── subtypes.tab_i.len
│   │   │   │   │   │   │       ├── supertypes.tab
│   │   │   │   │   │   │       ├── supertypes.tab.keystream
│   │   │   │   │   │   │       ├── supertypes.tab.keystream.len
│   │   │   │   │   │   │       ├── supertypes.tab.len
│   │   │   │   │   │   │       ├── supertypes.tab.values.at
│   │   │   │   │   │   │       ├── supertypes.tab_i
│   │   │   │   │   │   │       └── supertypes.tab_i.len
│   │   │   │   │   │   └── lookups/
│   │   │   │   │   │       ├── counters.tab
│   │   │   │   │   │       ├── file-to-id.tab
│   │   │   │   │   │       ├── file-to-id.tab.keystream
│   │   │   │   │   │       ├── file-to-id.tab.keystream.len
│   │   │   │   │   │       ├── file-to-id.tab.len
│   │   │   │   │   │       ├── file-to-id.tab.values.at
│   │   │   │   │   │       ├── file-to-id.tab_i
│   │   │   │   │   │       ├── file-to-id.tab_i.len
│   │   │   │   │   │       ├── id-to-file.tab
│   │   │   │   │   │       ├── id-to-file.tab.keystream
│   │   │   │   │   │       ├── id-to-file.tab.keystream.len
│   │   │   │   │   │       ├── id-to-file.tab.len
│   │   │   │   │   │       ├── id-to-file.tab.values.at
│   │   │   │   │   │       ├── id-to-file.tab_i
│   │   │   │   │   │       ├── id-to-file.tab_i.len
│   │   │   │   │   │       ├── lookups.tab
│   │   │   │   │   │       ├── lookups.tab.keystream
│   │   │   │   │   │       ├── lookups.tab.keystream.len
│   │   │   │   │   │       ├── lookups.tab.len
│   │   │   │   │   │       ├── lookups.tab.values.at
│   │   │   │   │   │       ├── lookups.tab_i
│   │   │   │   │   │       └── lookups.tab_i.len
│   │   │   │   │   └── last-build.bin
│   │   │   │   ├── classpath-snapshot/
│   │   │   │   │   └── shrunk-classpath-snapshot.bin
│   │   │   │   └── local-state/
│   │   │   ├── compileDebugUnitTestKotlin/
│   │   │   │   ├── cacheable/
│   │   │   │   │   ├── caches-jvm/
│   │   │   │   │   │   ├── compilerPluginFiles/
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │   │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │   │   │   ├── inputs/
│   │   │   │   │   │   │   ├── source-to-output.tab
│   │   │   │   │   │   │   ├── source-to-output.tab.keystream
│   │   │   │   │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │   │   │   │   ├── source-to-output.tab.len
│   │   │   │   │   │   │   ├── source-to-output.tab.values.at
│   │   │   │   │   │   │   ├── source-to-output.tab_i
│   │   │   │   │   │   │   └── source-to-output.tab_i.len
│   │   │   │   │   │   ├── jvm/
│   │   │   │   │   │   │   └── kotlin/
│   │   │   │   │   │   │       ├── class-attributes.tab
│   │   │   │   │   │   │       ├── class-attributes.tab.keystream
│   │   │   │   │   │   │       ├── class-attributes.tab.keystream.len
│   │   │   │   │   │   │       ├── class-attributes.tab.len
│   │   │   │   │   │   │       ├── class-attributes.tab.values.at
│   │   │   │   │   │   │       ├── class-attributes.tab_i
│   │   │   │   │   │   │       ├── class-attributes.tab_i.len
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.len
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.values.at
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i
│   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i.len
│   │   │   │   │   │   │       ├── internal-name-to-source.tab
│   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │   │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │   │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │   │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │   │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │   │   │   │       ├── proto.tab
│   │   │   │   │   │   │       ├── proto.tab.keystream
│   │   │   │   │   │   │       ├── proto.tab.keystream.len
│   │   │   │   │   │   │       ├── proto.tab.len
│   │   │   │   │   │   │       ├── proto.tab.values.at
│   │   │   │   │   │   │       ├── proto.tab_i
│   │   │   │   │   │   │       ├── proto.tab_i.len
│   │   │   │   │   │   │       ├── source-to-classes.tab
│   │   │   │   │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │   │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │   │   │   │       ├── source-to-classes.tab.len
│   │   │   │   │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │   │   │   │       ├── source-to-classes.tab_i
│   │   │   │   │   │   │       └── source-to-classes.tab_i.len
│   │   │   │   │   │   └── lookups/
│   │   │   │   │   │       ├── counters.tab
│   │   │   │   │   │       ├── file-to-id.tab
│   │   │   │   │   │       ├── file-to-id.tab.keystream
│   │   │   │   │   │       ├── file-to-id.tab.keystream.len
│   │   │   │   │   │       ├── file-to-id.tab.len
│   │   │   │   │   │       ├── file-to-id.tab.values.at
│   │   │   │   │   │       ├── file-to-id.tab_i
│   │   │   │   │   │       ├── file-to-id.tab_i.len
│   │   │   │   │   │       ├── id-to-file.tab
│   │   │   │   │   │       ├── id-to-file.tab.keystream
│   │   │   │   │   │       ├── id-to-file.tab.keystream.len
│   │   │   │   │   │       ├── id-to-file.tab.len
│   │   │   │   │   │       ├── id-to-file.tab.values.at
│   │   │   │   │   │       ├── id-to-file.tab_i.len
│   │   │   │   │   │       ├── lookups.tab
│   │   │   │   │   │       ├── lookups.tab.keystream
│   │   │   │   │   │       ├── lookups.tab.keystream.len
│   │   │   │   │   │       ├── lookups.tab.len
│   │   │   │   │   │       ├── lookups.tab.values.at
│   │   │   │   │   │       ├── lookups.tab_i
│   │   │   │   │   │       └── lookups.tab_i.len
│   │   │   │   │   └── last-build.bin
│   │   │   │   ├── classpath-snapshot/
│   │   │   │   │   └── shrunk-classpath-snapshot.bin
│   │   │   │   └── local-state/
│   │   │   └── compileReleaseKotlin/
│   │   │       ├── cacheable/
│   │   │       │   ├── caches-jvm/
│   │   │       │   │   ├── compilerPluginFiles/
│   │   │       │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │       │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │       │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │       │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │       │   │   ├── inputs/
│   │   │       │   │   │   ├── source-to-output.tab
│   │   │       │   │   │   ├── source-to-output.tab.keystream
│   │   │       │   │   │   ├── source-to-output.tab.keystream.len
│   │   │       │   │   │   ├── source-to-output.tab.len
│   │   │       │   │   │   ├── source-to-output.tab.values.at
│   │   │       │   │   │   ├── source-to-output.tab_i
│   │   │       │   │   │   └── source-to-output.tab_i.len
│   │   │       │   │   ├── jvm/
│   │   │       │   │   │   └── kotlin/
│   │   │       │   │   │       ├── class-attributes.tab
│   │   │       │   │   │       ├── class-attributes.tab.keystream
│   │   │       │   │   │       ├── class-attributes.tab.keystream.len
│   │   │       │   │   │       ├── class-attributes.tab.len
│   │   │       │   │   │       ├── class-attributes.tab.values.at
│   │   │       │   │   │       ├── class-attributes.tab_i
│   │   │       │   │   │       ├── class-attributes.tab_i.len
│   │   │       │   │   │       ├── class-fq-name-to-source.tab
│   │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream
│   │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│   │   │       │   │   │       ├── class-fq-name-to-source.tab.len
│   │   │       │   │   │       ├── class-fq-name-to-source.tab.values.at
│   │   │       │   │   │       ├── class-fq-name-to-source.tab_i
│   │   │       │   │   │       ├── class-fq-name-to-source.tab_i.len
│   │   │       │   │   │       ├── internal-name-to-source.tab
│   │   │       │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │       │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │       │   │   │       ├── internal-name-to-source.tab.len
│   │   │       │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │       │   │   │       ├── internal-name-to-source.tab_i
│   │   │       │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │       │   │   │       ├── package-parts.tab
│   │   │       │   │   │       ├── package-parts.tab.keystream
│   │   │       │   │   │       ├── package-parts.tab.keystream.len
│   │   │       │   │   │       ├── package-parts.tab.len
│   │   │       │   │   │       ├── package-parts.tab.values.at
│   │   │       │   │   │       ├── package-parts.tab_i
│   │   │       │   │   │       ├── package-parts.tab_i.len
│   │   │       │   │   │       ├── proto.tab
│   │   │       │   │   │       ├── proto.tab.keystream
│   │   │       │   │   │       ├── proto.tab.keystream.len
│   │   │       │   │   │       ├── proto.tab.len
│   │   │       │   │   │       ├── proto.tab.values.at
│   │   │       │   │   │       ├── proto.tab_i
│   │   │       │   │   │       ├── proto.tab_i.len
│   │   │       │   │   │       ├── source-to-classes.tab
│   │   │       │   │   │       ├── source-to-classes.tab.keystream
│   │   │       │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │       │   │   │       ├── source-to-classes.tab.len
│   │   │       │   │   │       ├── source-to-classes.tab.values.at
│   │   │       │   │   │       ├── source-to-classes.tab_i
│   │   │       │   │   │       ├── source-to-classes.tab_i.len
│   │   │       │   │   │       ├── subtypes.tab
│   │   │       │   │   │       ├── subtypes.tab.keystream
│   │   │       │   │   │       ├── subtypes.tab.keystream.len
│   │   │       │   │   │       ├── subtypes.tab.len
│   │   │       │   │   │       ├── subtypes.tab.values.at
│   │   │       │   │   │       ├── subtypes.tab_i
│   │   │       │   │   │       ├── subtypes.tab_i.len
│   │   │       │   │   │       ├── supertypes.tab
│   │   │       │   │   │       ├── supertypes.tab.keystream
│   │   │       │   │   │       ├── supertypes.tab.keystream.len
│   │   │       │   │   │       ├── supertypes.tab.len
│   │   │       │   │   │       ├── supertypes.tab.values.at
│   │   │       │   │   │       ├── supertypes.tab_i
│   │   │       │   │   │       └── supertypes.tab_i.len
│   │   │       │   │   └── lookups/
│   │   │       │   │       ├── counters.tab
│   │   │       │   │       ├── file-to-id.tab
│   │   │       │   │       ├── file-to-id.tab.keystream
│   │   │       │   │       ├── file-to-id.tab.keystream.len
│   │   │       │   │       ├── file-to-id.tab.len
│   │   │       │   │       ├── file-to-id.tab.values.at
│   │   │       │   │       ├── file-to-id.tab_i
│   │   │       │   │       ├── file-to-id.tab_i.len
│   │   │       │   │       ├── id-to-file.tab
│   │   │       │   │       ├── id-to-file.tab.keystream
│   │   │       │   │       ├── id-to-file.tab.keystream.len
│   │   │       │   │       ├── id-to-file.tab.len
│   │   │       │   │       ├── id-to-file.tab.values.at
│   │   │       │   │       ├── id-to-file.tab_i
│   │   │       │   │       ├── id-to-file.tab_i.len
│   │   │       │   │       ├── lookups.tab
│   │   │       │   │       ├── lookups.tab.keystream
│   │   │       │   │       ├── lookups.tab.keystream.len
│   │   │       │   │       ├── lookups.tab.len
│   │   │       │   │       ├── lookups.tab.values.at
│   │   │       │   │       ├── lookups.tab_i
│   │   │       │   │       └── lookups.tab_i.len
│   │   │       │   └── last-build.bin
│   │   │       ├── classpath-snapshot/
│   │   │       │   └── shrunk-classpath-snapshot.bin
│   │   │       └── local-state/
│   │   ├── kspCaches/
│   │   │   ├── debug/
│   │   │   │   ├── backups/java/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/
│   │   │   │   │   │   └── SafeCubeApp_GeneratedInjector.java
│   │   │   │   │   ├── dagger/hilt/internal/aggregatedroot/codegen/
│   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.java
│   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │       ├── _com_miguelrodriguez19_safecube_app_navigation_NavigationGatesEntryPoint.java
│   │   │   │   │       └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.java
│   │   │   │   ├── logs/
│   │   │   │   ├── symbolLookups/
│   │   │   │   │   ├── counters.tab
│   │   │   │   │   ├── file-to-id.tab
│   │   │   │   │   ├── file-to-id.tab.keystream
│   │   │   │   │   ├── file-to-id.tab.keystream.len
│   │   │   │   │   ├── file-to-id.tab.len
│   │   │   │   │   ├── file-to-id.tab.values.at
│   │   │   │   │   ├── file-to-id.tab_i
│   │   │   │   │   ├── file-to-id.tab_i.len
│   │   │   │   │   ├── id-to-file.tab
│   │   │   │   │   ├── id-to-file.tab.keystream
│   │   │   │   │   ├── id-to-file.tab.keystream.len
│   │   │   │   │   ├── id-to-file.tab.len
│   │   │   │   │   ├── id-to-file.tab.values.at
│   │   │   │   │   ├── id-to-file.tab_i
│   │   │   │   │   ├── id-to-file.tab_i.len
│   │   │   │   │   ├── lookups.tab
│   │   │   │   │   ├── lookups.tab.keystream
│   │   │   │   │   ├── lookups.tab.keystream.len
│   │   │   │   │   ├── lookups.tab.len
│   │   │   │   │   ├── lookups.tab.values.at
│   │   │   │   │   ├── lookups.tab_i
│   │   │   │   │   └── lookups.tab_i.len
│   │   │   │   ├── ap-classpath-entries.bin
│   │   │   │   ├── caches.uptodate
│   │   │   │   ├── classpath-entries.bin
│   │   │   │   ├── classpath-structure.bin
│   │   │   │   ├── sealed
│   │   │   │   ├── sourceToOutputs
│   │   │   │   └── symbols
│   │   │   └── release/
│   │   │       ├── backups/java/
│   │   │       │   ├── com/miguelrodriguez19/safecube/
│   │   │       │   │   └── SafeCubeApp_GeneratedInjector.java
│   │   │       │   ├── dagger/hilt/internal/aggregatedroot/codegen/
│   │   │       │   │   └── _com_miguelrodriguez19_safecube_SafeCubeApp.java
│   │   │       │   └── hilt_aggregated_deps/
│   │   │       │       ├── _com_miguelrodriguez19_safecube_app_navigation_NavigationGatesEntryPoint.java
│   │   │       │       └── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.java
│   │   │       ├── logs/
│   │   │       ├── symbolLookups/
│   │   │       │   ├── counters.tab
│   │   │       │   ├── file-to-id.tab
│   │   │       │   ├── file-to-id.tab.keystream
│   │   │       │   ├── file-to-id.tab.keystream.len
│   │   │       │   ├── file-to-id.tab.len
│   │   │       │   ├── file-to-id.tab.values.at
│   │   │       │   ├── file-to-id.tab_i
│   │   │       │   ├── file-to-id.tab_i.len
│   │   │       │   ├── id-to-file.tab
│   │   │       │   ├── id-to-file.tab.keystream
│   │   │       │   ├── id-to-file.tab.keystream.len
│   │   │       │   ├── id-to-file.tab.len
│   │   │       │   ├── id-to-file.tab.values.at
│   │   │       │   ├── id-to-file.tab_i
│   │   │       │   ├── id-to-file.tab_i.len
│   │   │       │   ├── lookups.tab
│   │   │       │   ├── lookups.tab.keystream
│   │   │       │   ├── lookups.tab.keystream.len
│   │   │       │   ├── lookups.tab.len
│   │   │       │   ├── lookups.tab.values.at
│   │   │       │   ├── lookups.tab_i
│   │   │       │   └── lookups.tab_i.len
│   │   │       ├── ap-classpath-entries.bin
│   │   │       ├── caches.uptodate
│   │   │       ├── classpath-entries.bin
│   │   │       ├── classpath-structure.bin
│   │   │       ├── sealed
│   │   │       ├── sourceToOutputs
│   │   │       └── symbols
│   │   ├── outputs/
│   │   │   ├── androidTest-results/connected/debug/
│   │   │   │   ├── Pixel_8a(AVD) - 16/
│   │   │   │   │   ├── testlog/
│   │   │   │   │   │   └── test-results.log
│   │   │   │   │   ├── aapt.1.ok.txt
│   │   │   │   │   ├── aapt.2.ok.txt
│   │   │   │   │   ├── cpuinfo
│   │   │   │   │   ├── device-info.pb
│   │   │   │   │   ├── logcat-com.miguelrodriguez19.safecube.ExampleInstrumentedTest-useAppContext.txt
│   │   │   │   │   ├── logcat-com.miguelrodriguez19.safecube.MainActivityComposeTest-greeting_isVisible.txt
│   │   │   │   │   ├── meminfo
│   │   │   │   │   ├── test-result.pb
│   │   │   │   │   ├── test-result.textproto
│   │   │   │   │   ├── utp.0.log
│   │   │   │   │   └── utp.0.log.lck
│   │   │   │   ├── SM-A325F - 13/
│   │   │   │   │   ├── testlog/
│   │   │   │   │   │   └── test-results.log
│   │   │   │   │   ├── aapt.1.ok.txt
│   │   │   │   │   ├── aapt.2.ok.txt
│   │   │   │   │   ├── cpuinfo
│   │   │   │   │   ├── device-info.pb
│   │   │   │   │   ├── logcat-com.miguelrodriguez19.safecube.ExampleInstrumentedTest-useAppContext.txt
│   │   │   │   │   ├── meminfo
│   │   │   │   │   ├── test-result.pb
│   │   │   │   │   ├── test-result.textproto
│   │   │   │   │   ├── utp.0.log
│   │   │   │   │   └── utp.0.log.lck
│   │   │   │   ├── TEST-Pixel_8a(AVD) - 16-_app-.xml
│   │   │   │   ├── test-result-exit-code.txt
│   │   │   │   ├── test-result.pb
│   │   │   │   └── TEST-SM-A325F - 13-_app-.xml
│   │   │   ├── apk/
│   │   │   │   ├── androidTest/debug/
│   │   │   │   │   ├── app-debug-androidTest.apk
│   │   │   │   │   └── output-metadata.json
│   │   │   │   └── debug/
│   │   │   │       ├── app-debug.apk
│   │   │   │       └── output-metadata.json
│   │   │   ├── code_coverage/debugAndroidTest/connected/
│   │   │   ├── connected_android_test_additional_output/debugAndroidTest/connected/
│   │   │   │   ├── Pixel_8a(AVD) - 16/
│   │   │   │   └── SM-A325F - 13/
│   │   │   ├── logs/
│   │   │   │   ├── manifest-merger-debug-report.txt
│   │   │   │   └── manifest-merger-release-report.txt
│   │   │   ├── mapping/release/
│   │   │   │   ├── configuration.txt
│   │   │   │   ├── mapping.txt
│   │   │   │   ├── resources.txt
│   │   │   │   ├── seeds.txt
│   │   │   │   └── usage.txt
│   │   │   └── sdk-dependencies/release/
│   │   │       └── sdkDependencies.txt
│   │   ├── reports/
│   │   │   ├── androidTests/connected/debug/
│   │   │   │   ├── css/
│   │   │   │   │   ├── base-style.css
│   │   │   │   │   └── style.css
│   │   │   │   ├── js/
│   │   │   │   │   └── report.js
│   │   │   │   ├── com.miguelrodriguez19.safecube.ExampleInstrumentedTest.html
│   │   │   │   ├── com.miguelrodriguez19.safecube.html
│   │   │   │   ├── com.miguelrodriguez19.safecube.MainActivityComposeTest.html
│   │   │   │   └── index.html
│   │   │   ├── resources_config_map_file/release/
│   │   │   │   └── resources.cfg
│   │   │   └── tests/testDebugUnitTest/
│   │   │       ├── classes/
│   │   │       │   └── com.miguelrodriguez19.safecube.TestingSetupUnitTest.html
│   │   │       ├── css/
│   │   │       │   ├── base-style.css
│   │   │       │   └── style.css
│   │   │       ├── js/
│   │   │       │   └── report.js
│   │   │       ├── packages/
│   │   │       │   └── com.miguelrodriguez19.safecube.html
│   │   │       └── index.html
│   │   ├── test-results/testDebugUnitTest/
│   │   │   ├── binary/
│   │   │   │   ├── output.bin
│   │   │   │   ├── output.bin.idx
│   │   │   │   └── results.bin
│   │   │   └── TEST-com.miguelrodriguez19.safecube.TestingSetupUnitTest.xml
│   │   └── tmp/
│   │       ├── compileDebugJavaWithJavac/
│   │       │   ├── compileTransaction/
│   │       │   │   ├── backup-dir/
│   │       │   │   └── stash-dir/
│   │       │   └── previous-compilation-data.bin
│   │       ├── compileReleaseJavaWithJavac/
│   │       │   ├── compileTransaction/
│   │       │   │   ├── backup-dir/
│   │       │   │   └── stash-dir/
│   │       │   └── previous-compilation-data.bin
│   │       ├── hiltJavaCompileDebug/
│   │       │   ├── compileTransaction/
│   │       │   │   ├── backup-dir/
│   │       │   │   │   ├── _com_miguelrodriguez19_safecube_SafeCubeApp.class2
│   │       │   │   │   ├── _com_miguelrodriguez19_safecube_SafeCubeApp_GeneratedInjector.class3
│   │       │   │   │   └── SafeCubeApp_GeneratedInjector.class1
│   │       │   │   └── stash-dir/
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityCBuilder.class.uniqueId38
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityCImpl.class.uniqueId8
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCBuilder.class.uniqueId9
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCImpl$SwitchingProvider.class.uniqueId20
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCImpl.class.uniqueId43
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$Builder.class.uniqueId44
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$FragmentCBuilder.class.uniqueId30
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$FragmentCImpl.class.uniqueId2
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ServiceCBuilder.class.uniqueId11
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ServiceCImpl.class.uniqueId27
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$SingletonCImpl$SwitchingProvider.class.uniqueId16
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$SingletonCImpl.class.uniqueId46
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewCBuilder.class.uniqueId3
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewCImpl.class.uniqueId36
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewModelCBuilder.class.uniqueId0
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewModelCImpl.class.uniqueId7
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewWithFragmentCBuilder.class.uniqueId10
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewWithFragmentCImpl.class.uniqueId15
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC.class.uniqueId29
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC.java.uniqueId45
│   │       │   │       ├── Hilt_SafeCubeApp$1.class.uniqueId13
│   │       │   │       ├── Hilt_SafeCubeApp.class.uniqueId6
│   │       │   │       ├── Hilt_SafeCubeApp.java.uniqueId40
│   │       │   │       ├── SafeCubeApp_HiltComponents$ActivityC$Builder.class.uniqueId19
│   │       │   │       ├── SafeCubeApp_HiltComponents$ActivityC.class.uniqueId17
│   │       │   │       ├── SafeCubeApp_HiltComponents$ActivityCBuilderModule.class.uniqueId12
│   │       │   │       ├── SafeCubeApp_HiltComponents$ActivityRetainedC$Builder.class.uniqueId34
│   │       │   │       ├── SafeCubeApp_HiltComponents$ActivityRetainedC.class.uniqueId22
│   │       │   │       ├── SafeCubeApp_HiltComponents$ActivityRetainedCBuilderModule.class.uniqueId37
│   │       │   │       ├── SafeCubeApp_HiltComponents$FragmentC$Builder.class.uniqueId28
│   │       │   │       ├── SafeCubeApp_HiltComponents$FragmentC.class.uniqueId42
│   │       │   │       ├── SafeCubeApp_HiltComponents$FragmentCBuilderModule.class.uniqueId35
│   │       │   │       ├── SafeCubeApp_HiltComponents$ServiceC$Builder.class.uniqueId14
│   │       │   │       ├── SafeCubeApp_HiltComponents$ServiceC.class.uniqueId31
│   │       │   │       ├── SafeCubeApp_HiltComponents$ServiceCBuilderModule.class.uniqueId23
│   │       │   │       ├── SafeCubeApp_HiltComponents$SingletonC.class.uniqueId5
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewC$Builder.class.uniqueId32
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewC.class.uniqueId18
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewCBuilderModule.class.uniqueId33
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewModelC$Builder.class.uniqueId1
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewModelC.class.uniqueId25
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewModelCBuilderModule.class.uniqueId41
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewWithFragmentC$Builder.class.uniqueId4
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewWithFragmentC.class.uniqueId26
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewWithFragmentCBuilderModule.class.uniqueId24
│   │       │   │       ├── SafeCubeApp_HiltComponents.class.uniqueId39
│   │       │   │       └── SafeCubeApp_HiltComponents.java.uniqueId21
│   │       │   └── previous-compilation-data.bin
│   │       ├── hiltJavaCompileRelease/
│   │       │   ├── compileTransaction/
│   │       │   │   ├── backup-dir/
│   │       │   │   └── stash-dir/
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityCBuilder.class.uniqueId10
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityCImpl.class.uniqueId44
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCBuilder.class.uniqueId26
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCImpl$SwitchingProvider.class.uniqueId3
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ActivityRetainedCImpl.class.uniqueId24
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$Builder.class.uniqueId21
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$FragmentCBuilder.class.uniqueId8
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$FragmentCImpl.class.uniqueId39
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ServiceCBuilder.class.uniqueId42
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ServiceCImpl.class.uniqueId11
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$SingletonCImpl$SwitchingProvider.class.uniqueId6
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$SingletonCImpl.class.uniqueId2
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewCBuilder.class.uniqueId16
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewCImpl.class.uniqueId0
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewModelCBuilder.class.uniqueId30
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewModelCImpl.class.uniqueId25
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewWithFragmentCBuilder.class.uniqueId43
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC$ViewWithFragmentCImpl.class.uniqueId41
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC.class.uniqueId19
│   │       │   │       ├── DaggerSafeCubeApp_HiltComponents_SingletonC.java.uniqueId29
│   │       │   │       ├── Hilt_SafeCubeApp$1.class.uniqueId22
│   │       │   │       ├── Hilt_SafeCubeApp.class.uniqueId40
│   │       │   │       ├── Hilt_SafeCubeApp.java.uniqueId27
│   │       │   │       ├── SafeCubeApp_HiltComponents$ActivityC$Builder.class.uniqueId1
│   │       │   │       ├── SafeCubeApp_HiltComponents$ActivityC.class.uniqueId32
│   │       │   │       ├── SafeCubeApp_HiltComponents$ActivityCBuilderModule.class.uniqueId37
│   │       │   │       ├── SafeCubeApp_HiltComponents$ActivityRetainedC$Builder.class.uniqueId9
│   │       │   │       ├── SafeCubeApp_HiltComponents$ActivityRetainedC.class.uniqueId36
│   │       │   │       ├── SafeCubeApp_HiltComponents$ActivityRetainedCBuilderModule.class.uniqueId23
│   │       │   │       ├── SafeCubeApp_HiltComponents$FragmentC$Builder.class.uniqueId7
│   │       │   │       ├── SafeCubeApp_HiltComponents$FragmentC.class.uniqueId5
│   │       │   │       ├── SafeCubeApp_HiltComponents$FragmentCBuilderModule.class.uniqueId17
│   │       │   │       ├── SafeCubeApp_HiltComponents$ServiceC$Builder.class.uniqueId13
│   │       │   │       ├── SafeCubeApp_HiltComponents$ServiceC.class.uniqueId38
│   │       │   │       ├── SafeCubeApp_HiltComponents$ServiceCBuilderModule.class.uniqueId15
│   │       │   │       ├── SafeCubeApp_HiltComponents$SingletonC.class.uniqueId34
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewC$Builder.class.uniqueId33
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewC.class.uniqueId18
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewCBuilderModule.class.uniqueId46
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewModelC$Builder.class.uniqueId45
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewModelC.class.uniqueId31
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewModelCBuilderModule.class.uniqueId14
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewWithFragmentC$Builder.class.uniqueId28
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewWithFragmentC.class.uniqueId4
│   │       │   │       ├── SafeCubeApp_HiltComponents$ViewWithFragmentCBuilderModule.class.uniqueId35
│   │       │   │       ├── SafeCubeApp_HiltComponents.class.uniqueId12
│   │       │   │       └── SafeCubeApp_HiltComponents.java.uniqueId20
│   │       │   └── previous-compilation-data.bin
│   │       └── testDebugUnitTest/
│   ├── src/
│   │   ├── androidTest/java/com/miguelrodriguez19/safecube/
│   │   │   ├── ExampleInstrumentedTest.kt
│   │   │   └── MainActivityComposeTest.kt
│   │   ├── main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/
│   │   │   │   ├── app/navigation/
│   │   │   │   │   ├── NavigationGates.kt
│   │   │   │   │   ├── NavigationWrapper.kt
│   │   │   │   │   └── Routes.kt
│   │   │   │   ├── core/
│   │   │   │   ├── ui/theme/
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Theme.kt
│   │   │   │   │   └── Type.kt
│   │   │   │   ├── view/core/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── SafeCubeApp.kt
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── ic_launcher_background.xml
│   │   │   │   │   └── ic_launcher_foreground.xml
│   │   │   │   ├── mipmap-anydpi/
│   │   │   │   │   ├── ic_launcher.xml
│   │   │   │   │   └── ic_launcher_round.xml
│   │   │   │   ├── mipmap-hdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-mdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-xhdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-xxhdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-xxxhdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── values/
│   │   │   │   │   └── strings.xml
│   │   │   │   └── xml/
│   │   │   │       ├── backup_rules.xml
│   │   │   │       └── data_extraction_rules.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/java/com/miguelrodriguez19/safecube/
│   │       └── ExampleUnitTest.kt
│   ├── .gitignore
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build/reports/problems/
│   └── problems-report.html
├── core/
│   ├── auth/
│   │   ├── build/
│   │   │   ├── .transforms/
│   │   │   │   ├── 01dbcda035490effcf3b90afe24e0f0e/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   └── AuthModule.dex
│   │   │   │   │   │   │   ├── internal/
│   │   │   │   │   │   │   │   ├── EncryptedTokenStorage$Companion.dex
│   │   │   │   │   │   │   │   ├── EncryptedTokenStorage.dex
│   │   │   │   │   │   │   │   ├── EncryptedTokenStorage_Factory.dex
│   │   │   │   │   │   │   │   ├── FakeVaultSessionManager.dex
│   │   │   │   │   │   │   │   ├── FakeVaultSessionManager_Factory$InstanceHolder.dex
│   │   │   │   │   │   │   │   └── FakeVaultSessionManager_Factory.dex
│   │   │   │   │   │   │   ├── AuthRepository.dex
│   │   │   │   │   │   │   ├── SessionManager$logout$1.dex
│   │   │   │   │   │   │   ├── SessionManager$refreshSessionState$1.dex
│   │   │   │   │   │   │   ├── SessionManager.dex
│   │   │   │   │   │   │   ├── SessionManager_Factory.dex
│   │   │   │   │   │   │   ├── SessionState$LoggedIn.dex
│   │   │   │   │   │   │   ├── SessionState$LoggedOut.dex
│   │   │   │   │   │   │   ├── SessionState$Unknown.dex
│   │   │   │   │   │   │   ├── SessionState.dex
│   │   │   │   │   │   │   ├── TokenStorage.dex
│   │   │   │   │   │   │   ├── VaultSessionManager.dex
│   │   │   │   │   │   │   ├── VaultState$Locked.dex
│   │   │   │   │   │   │   ├── VaultState$NotInitialized.dex
│   │   │   │   │   │   │   ├── VaultState$Unknown.dex
│   │   │   │   │   │   │   ├── VaultState$Unlocked.dex
│   │   │   │   │   │   │   └── VaultState.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_auth_di_AuthModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 079835751bcff0d3baa656acb4548146/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 0c6bf9205c683d09b8da0c67d1845e1b/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   └── AuthModule.dex
│   │   │   │   │   │   │   ├── internal/
│   │   │   │   │   │   │   │   ├── EncryptedTokenStorage$Companion.dex
│   │   │   │   │   │   │   │   ├── EncryptedTokenStorage.dex
│   │   │   │   │   │   │   │   ├── EncryptedTokenStorage_Factory.dex
│   │   │   │   │   │   │   │   ├── FakeVaultSessionManager.dex
│   │   │   │   │   │   │   │   ├── FakeVaultSessionManager_Factory$InstanceHolder.dex
│   │   │   │   │   │   │   │   └── FakeVaultSessionManager_Factory.dex
│   │   │   │   │   │   │   ├── AuthError$AccountAlreadyExists.dex
│   │   │   │   │   │   │   ├── AuthError$AccountNotActive.dex
│   │   │   │   │   │   │   ├── AuthError$Conflict.dex
│   │   │   │   │   │   │   ├── AuthError$Forbidden.dex
│   │   │   │   │   │   │   ├── AuthError$InvalidCredentials.dex
│   │   │   │   │   │   │   ├── AuthError$Unknown.dex
│   │   │   │   │   │   │   ├── AuthError$ValidationFailed.dex
│   │   │   │   │   │   │   ├── AuthError.dex
│   │   │   │   │   │   │   ├── AuthErrorMapper$ParsedErrorBody.dex
│   │   │   │   │   │   │   ├── AuthErrorMapper.dex
│   │   │   │   │   │   │   ├── AuthErrorMapper_Factory$InstanceHolder.dex
│   │   │   │   │   │   │   ├── AuthErrorMapper_Factory.dex
│   │   │   │   │   │   │   ├── AuthOperation.dex
│   │   │   │   │   │   │   ├── AuthRepository.dex
│   │   │   │   │   │   │   ├── SessionManager.dex
│   │   │   │   │   │   │   ├── SessionManager_Factory.dex
│   │   │   │   │   │   │   ├── SessionState$LoggedIn.dex
│   │   │   │   │   │   │   ├── SessionState$LoggedOut.dex
│   │   │   │   │   │   │   ├── SessionState.dex
│   │   │   │   │   │   │   ├── TokenStorage.dex
│   │   │   │   │   │   │   ├── VaultSessionManager.dex
│   │   │   │   │   │   │   ├── VaultState$Locked.dex
│   │   │   │   │   │   │   ├── VaultState$NotInitialized.dex
│   │   │   │   │   │   │   ├── VaultState$Unknown.dex
│   │   │   │   │   │   │   ├── VaultState$Unlocked.dex
│   │   │   │   │   │   │   └── VaultState.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_auth_di_AuthModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 59766b2d88a51cd7fac0f9af89aa509d/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── b406348265136ff622eb6a11e307ebc5/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── b574b43e43b6fe3c25ab06b9333bcbcb/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── c8a149c785dacfa54d3c726bf4fd5ab2/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── d5ca3bc72b13051a289f45400d208e39/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   └── AuthModule.dex
│   │   │   │   │   │   │   ├── internal/
│   │   │   │   │   │   │   │   ├── EncryptedTokenStorage$Companion.dex
│   │   │   │   │   │   │   │   ├── EncryptedTokenStorage.dex
│   │   │   │   │   │   │   │   └── EncryptedTokenStorage_Factory.dex
│   │   │   │   │   │   │   └── TokenStorage.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_auth_di_AuthModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── d5df085779dba2f23a6e9dbab3983fb1/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/di/
│   │   │   │   │   │   │   └── AuthModule.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_auth_di_AuthModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── f5024833c8f5061585ecdb3f93a64e61/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── f7da16d22b7523a272cd87f28dc7ec79/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   └── fc980db27a66f3b78763bd405b6310cf/
│   │   │   │       ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │       │   └── desugar_graph.bin
│   │   │   │       └── results.bin
│   │   │   ├── generated/
│   │   │   │   ├── ap_generated_sources/
│   │   │   │   │   ├── debug/out/
│   │   │   │   │   └── release/out/
│   │   │   │   ├── hilt/component_trees/debugUnitTest/
│   │   │   │   ├── ksp/
│   │   │   │   │   ├── debug/java/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   │   │   ├── internal/
│   │   │   │   │   │   │   │   ├── EncryptedTokenStorage_Factory.java
│   │   │   │   │   │   │   │   └── FakeVaultSessionManager_Factory.java
│   │   │   │   │   │   │   ├── AuthErrorMapper_Factory.java
│   │   │   │   │   │   │   ├── AuthRepositoryImpl_Factory.java
│   │   │   │   │   │   │   ├── RemoteAuthDataSource_Factory.java
│   │   │   │   │   │   │   └── SessionManager_Factory.java
│   │   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_core_auth_di_AuthModule.java
│   │   │   │   │   └── release/java/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │       │   ├── internal/
│   │   │   │   │       │   │   ├── EncryptedTokenStorage_Factory.java
│   │   │   │   │       │   │   └── FakeVaultSessionManager_Factory.java
│   │   │   │   │       │   └── SessionManager_Factory.java
│   │   │   │   │       └── hilt_aggregated_deps/
│   │   │   │   │           └── _com_miguelrodriguez19_safecube_core_auth_di_AuthModule.java
│   │   │   │   ├── res/pngs/
│   │   │   │   │   ├── debug/
│   │   │   │   │   └── release/
│   │   │   │   └── updated_navigation_xml/
│   │   │   │       ├── debug/
│   │   │   │       ├── debugAndroidTest/
│   │   │   │       └── release/
│   │   │   ├── intermediates/
│   │   │   │   ├── aapt_friendly_merged_manifests/
│   │   │   │   │   ├── debug/processDebugManifest/aapt/
│   │   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   │   └── output-metadata.json
│   │   │   │   │   └── release/processReleaseManifest/aapt/
│   │   │   │   │       ├── AndroidManifest.xml
│   │   │   │   │       └── output-metadata.json
│   │   │   │   ├── aar_libs_directory/debug/syncDebugLibJars/libs/
│   │   │   │   ├── aar_main_jar/debug/syncDebugLibJars/
│   │   │   │   │   └── classes.jar
│   │   │   │   ├── aar_metadata/
│   │   │   │   │   ├── debug/writeDebugAarMetadata/
│   │   │   │   │   │   │   │   │   │   │   └── release/writeReleaseAarMetadata/
│   │   │   │   │       │   │   │   │   ├── aar_metadata_check/
│   │   │   │   │   ├── debug/checkDebugAarMetadata/
│   │   │   │   │   └── debugAndroidTest/checkDebugAndroidTestAarMetadata/
│   │   │   │   ├── android_res_source_set_path_map/debugAndroidTest/mapDebugAndroidTestSourceSetPaths/
│   │   │   │   │   └── file-map.txt
│   │   │   │   ├── annotation_processor_list/
│   │   │   │   │   ├── debug/javaPreCompileDebug/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   ├── debugAndroidTest/javaPreCompileDebugAndroidTest/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   ├── debugUnitTest/javaPreCompileDebugUnitTest/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   └── release/javaPreCompileRelease/
│   │   │   │   │       └── annotationProcessors.json
│   │   │   │   ├── annotations_typedef_file/debug/extractDebugAnnotations/
│   │   │   │   │   └── typedefs.txt
│   │   │   │   ├── annotations_zip/debug/extractDebugAnnotations/
│   │   │   │   ├── apk_ide_redirect_file/debugAndroidTest/createDebugAndroidTestApkListingFileRedirect/
│   │   │   │   │   └── redirect.txt
│   │   │   │   ├── assets/
│   │   │   │   │   ├── debug/mergeDebugAssets/
│   │   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestAssets/
│   │   │   │   │   │   └── PublicSuffixDatabase.list
│   │   │   │   │   └── release/mergeReleaseAssets/
│   │   │   │   ├── built_in_kotlinc/
│   │   │   │   │   ├── debug/compileDebugKotlin/classes/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   └── AuthModule.class
│   │   │   │   │   │   │   ├── internal/
│   │   │   │   │   │   │   │   ├── EncryptedTokenStorage$Companion.class
│   │   │   │   │   │   │   │   ├── EncryptedTokenStorage.class
│   │   │   │   │   │   │   │   └── FakeVaultSessionManager.class
│   │   │   │   │   │   │   ├── AuthError$AccountAlreadyExists.class
│   │   │   │   │   │   │   ├── AuthError$AccountNotActive.class
│   │   │   │   │   │   │   ├── AuthError$Conflict.class
│   │   │   │   │   │   │   ├── AuthError$Forbidden.class
│   │   │   │   │   │   │   ├── AuthError$InvalidCredentials.class
│   │   │   │   │   │   │   ├── AuthError$Unknown.class
│   │   │   │   │   │   │   ├── AuthError$ValidationFailed.class
│   │   │   │   │   │   │   ├── AuthError.class
│   │   │   │   │   │   │   ├── AuthErrorMapper$ParsedErrorBody.class
│   │   │   │   │   │   │   ├── AuthErrorMapper.class
│   │   │   │   │   │   │   ├── AuthOperation.class
│   │   │   │   │   │   │   ├── AuthRepository$DefaultImpls.class
│   │   │   │   │   │   │   ├── AuthRepository.class
│   │   │   │   │   │   │   ├── AuthRepositoryImpl$login$1.class
│   │   │   │   │   │   │   ├── AuthRepositoryImpl$logout$1.class
│   │   │   │   │   │   │   ├── AuthRepositoryImpl$refresh$1.class
│   │   │   │   │   │   │   ├── AuthRepositoryImpl$register$1.class
│   │   │   │   │   │   │   ├── AuthRepositoryImpl.class
│   │   │   │   │   │   │   ├── AuthResult$Error.class
│   │   │   │   │   │   │   ├── AuthResult$Success.class
│   │   │   │   │   │   │   ├── AuthResult.class
│   │   │   │   │   │   │   ├── AuthTokens.class
│   │   │   │   │   │   │   ├── NetworkResult$Failure.class
│   │   │   │   │   │   │   ├── NetworkResult$HttpError.class
│   │   │   │   │   │   │   ├── NetworkResult$Success.class
│   │   │   │   │   │   │   ├── NetworkResult.class
│   │   │   │   │   │   │   ├── RegisteredAccount.class
│   │   │   │   │   │   │   ├── RemoteAuthDataSource$execute$1.class
│   │   │   │   │   │   │   ├── RemoteAuthDataSource$login$2.class
│   │   │   │   │   │   │   ├── RemoteAuthDataSource$logout$2.class
│   │   │   │   │   │   │   ├── RemoteAuthDataSource$refresh$2.class
│   │   │   │   │   │   │   ├── RemoteAuthDataSource$register$2.class
│   │   │   │   │   │   │   ├── RemoteAuthDataSource.class
│   │   │   │   │   │   │   ├── SessionManager.class
│   │   │   │   │   │   │   ├── SessionState$LoggedIn.class
│   │   │   │   │   │   │   ├── SessionState$LoggedOut.class
│   │   │   │   │   │   │   ├── SessionState.class
│   │   │   │   │   │   │   ├── TokenStorage.class
│   │   │   │   │   │   │   ├── VaultSessionManager.class
│   │   │   │   │   │   │   ├── VaultState$Locked.class
│   │   │   │   │   │   │   ├── VaultState$NotInitialized.class
│   │   │   │   │   │   │   ├── VaultState$Unknown.class
│   │   │   │   │   │   │   ├── VaultState$Unlocked.class
│   │   │   │   │   │   │   └── VaultState.class
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── auth.kotlin_module
│   │   │   │   │   ├── debugUnitTest/compileDebugUnitTestKotlin/classes/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   │   │   ├── AuthErrorMapperTest.class
│   │   │   │   │   │   │   ├── AuthRepositoryFakeAuthControllerApi.class
│   │   │   │   │   │   │   ├── AuthRepositoryImplTest$login maps successful response into domain auth tokens$1.class
│   │   │   │   │   │   │   ├── AuthRepositoryImplTest$logout maps transport failure into unknown auth error$1.class
│   │   │   │   │   │   │   ├── AuthRepositoryImplTest$refresh maps 401 into invalid credentials$1.class
│   │   │   │   │   │   │   ├── AuthRepositoryImplTest$register maps 409 into account already exists$1.class
│   │   │   │   │   │   │   ├── AuthRepositoryImplTest$register maps successful response into domain account$1.class
│   │   │   │   │   │   │   ├── AuthRepositoryImplTest.class
│   │   │   │   │   │   │   ├── FakeAuthControllerApi.class
│   │   │   │   │   │   │   ├── RemoteAuthDataSourceTest$login returns success result preserving code and body$1.class
│   │   │   │   │   │   │   ├── RemoteAuthDataSourceTest$refresh wraps transport exceptions into failure result$1.class
│   │   │   │   │   │   │   ├── RemoteAuthDataSourceTest$register returns http error preserving code and error body$1.class
│   │   │   │   │   │   │   └── RemoteAuthDataSourceTest.class
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── auth.kotlin_module
│   │   │   │   │   └── release/compileReleaseKotlin/classes/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │       │   ├── di/
│   │   │   │   │       │   │   └── AuthModule.class
│   │   │   │   │       │   ├── internal/
│   │   │   │   │       │   │   ├── EncryptedTokenStorage$Companion.class
│   │   │   │   │       │   │   ├── EncryptedTokenStorage.class
│   │   │   │   │       │   │   └── FakeVaultSessionManager.class
│   │   │   │   │       │   ├── AuthRepository.class
│   │   │   │   │       │   ├── SessionManager.class
│   │   │   │   │       │   ├── SessionState$LoggedIn.class
│   │   │   │   │       │   ├── SessionState$LoggedOut.class
│   │   │   │   │       │   ├── SessionState.class
│   │   │   │   │       │   ├── TokenStorage.class
│   │   │   │   │       │   ├── VaultSessionManager.class
│   │   │   │   │       │   ├── VaultState$Locked.class
│   │   │   │   │       │   ├── VaultState$NotInitialized.class
│   │   │   │   │       │   ├── VaultState$Unknown.class
│   │   │   │   │       │   ├── VaultState$Unlocked.class
│   │   │   │   │       │   └── VaultState.class
│   │   │   │   │       └── META-INF/
│   │   │   │   │           └── auth.kotlin_module
│   │   │   │   ├── classes/
│   │   │   │   │   ├── debug/transformDebugClassesWithAsm/
│   │   │   │   │   │   ├── dirs/
│   │   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   │   └── AuthModule.class
│   │   │   │   │   │   │   │   ├── internal/
│   │   │   │   │   │   │   │   │   ├── EncryptedTokenStorage$Companion.class
│   │   │   │   │   │   │   │   │   ├── EncryptedTokenStorage.class
│   │   │   │   │   │   │   │   │   ├── EncryptedTokenStorage_Factory.class
│   │   │   │   │   │   │   │   │   ├── FakeVaultSessionManager.class
│   │   │   │   │   │   │   │   │   ├── FakeVaultSessionManager_Factory$InstanceHolder.class
│   │   │   │   │   │   │   │   │   └── FakeVaultSessionManager_Factory.class
│   │   │   │   │   │   │   │   ├── AuthError$AccountAlreadyExists.class
│   │   │   │   │   │   │   │   ├── AuthError$AccountNotActive.class
│   │   │   │   │   │   │   │   ├── AuthError$Conflict.class
│   │   │   │   │   │   │   │   ├── AuthError$Forbidden.class
│   │   │   │   │   │   │   │   ├── AuthError$InvalidCredentials.class
│   │   │   │   │   │   │   │   ├── AuthError$Unknown.class
│   │   │   │   │   │   │   │   ├── AuthError$ValidationFailed.class
│   │   │   │   │   │   │   │   ├── AuthError.class
│   │   │   │   │   │   │   │   ├── AuthErrorMapper$ParsedErrorBody.class
│   │   │   │   │   │   │   │   ├── AuthErrorMapper.class
│   │   │   │   │   │   │   │   ├── AuthErrorMapper_Factory$InstanceHolder.class
│   │   │   │   │   │   │   │   ├── AuthErrorMapper_Factory.class
│   │   │   │   │   │   │   │   ├── AuthOperation.class
│   │   │   │   │   │   │   │   ├── AuthRepository$DefaultImpls.class
│   │   │   │   │   │   │   │   ├── AuthRepository.class
│   │   │   │   │   │   │   │   ├── AuthRepositoryImpl$login$1.class
│   │   │   │   │   │   │   │   ├── AuthRepositoryImpl$logout$1.class
│   │   │   │   │   │   │   │   ├── AuthRepositoryImpl$refresh$1.class
│   │   │   │   │   │   │   │   ├── AuthRepositoryImpl$register$1.class
│   │   │   │   │   │   │   │   ├── AuthRepositoryImpl.class
│   │   │   │   │   │   │   │   ├── AuthRepositoryImpl_Factory.class
│   │   │   │   │   │   │   │   ├── AuthResult$Error.class
│   │   │   │   │   │   │   │   ├── AuthResult$Success.class
│   │   │   │   │   │   │   │   ├── AuthResult.class
│   │   │   │   │   │   │   │   ├── AuthTokens.class
│   │   │   │   │   │   │   │   ├── NetworkResult$Failure.class
│   │   │   │   │   │   │   │   ├── NetworkResult$HttpError.class
│   │   │   │   │   │   │   │   ├── NetworkResult$Success.class
│   │   │   │   │   │   │   │   ├── NetworkResult.class
│   │   │   │   │   │   │   │   ├── RegisteredAccount.class
│   │   │   │   │   │   │   │   ├── RemoteAuthDataSource$execute$1.class
│   │   │   │   │   │   │   │   ├── RemoteAuthDataSource$login$2.class
│   │   │   │   │   │   │   │   ├── RemoteAuthDataSource$logout$2.class
│   │   │   │   │   │   │   │   ├── RemoteAuthDataSource$refresh$2.class
│   │   │   │   │   │   │   │   ├── RemoteAuthDataSource$register$2.class
│   │   │   │   │   │   │   │   ├── RemoteAuthDataSource.class
│   │   │   │   │   │   │   │   ├── RemoteAuthDataSource_Factory.class
│   │   │   │   │   │   │   │   ├── SessionManager.class
│   │   │   │   │   │   │   │   ├── SessionManager_Factory.class
│   │   │   │   │   │   │   │   ├── SessionState$LoggedIn.class
│   │   │   │   │   │   │   │   ├── SessionState$LoggedOut.class
│   │   │   │   │   │   │   │   ├── SessionState.class
│   │   │   │   │   │   │   │   ├── TokenStorage.class
│   │   │   │   │   │   │   │   ├── VaultSessionManager.class
│   │   │   │   │   │   │   │   ├── VaultState$Locked.class
│   │   │   │   │   │   │   │   ├── VaultState$NotInitialized.class
│   │   │   │   │   │   │   │   ├── VaultState$Unknown.class
│   │   │   │   │   │   │   │   ├── VaultState$Unlocked.class
│   │   │   │   │   │   │   │   └── VaultState.class
│   │   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_auth_di_AuthModule.class
│   │   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │   │       └── auth.kotlin_module
│   │   │   │   │   │   └── jars/
│   │   │   │   │   ├── debugUnitTest/transformDebugUnitTestClassesWithAsm/
│   │   │   │   │   │   ├── dirs/
│   │   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   │   │   │   ├── AuthErrorMapperTest.class
│   │   │   │   │   │   │   │   ├── AuthRepositoryFakeAuthControllerApi.class
│   │   │   │   │   │   │   │   ├── AuthRepositoryImplTest$login maps successful response into domain auth tokens$1.class
│   │   │   │   │   │   │   │   ├── AuthRepositoryImplTest$logout maps transport failure into unknown auth error$1.class
│   │   │   │   │   │   │   │   ├── AuthRepositoryImplTest$refresh maps 401 into invalid credentials$1.class
│   │   │   │   │   │   │   │   ├── AuthRepositoryImplTest$register maps 409 into account already exists$1.class
│   │   │   │   │   │   │   │   ├── AuthRepositoryImplTest$register maps successful response into domain account$1.class
│   │   │   │   │   │   │   │   ├── AuthRepositoryImplTest.class
│   │   │   │   │   │   │   │   ├── FakeAuthControllerApi.class
│   │   │   │   │   │   │   │   ├── RemoteAuthDataSourceTest$login returns success result preserving code and body$1.class
│   │   │   │   │   │   │   │   ├── RemoteAuthDataSourceTest$refresh wraps transport exceptions into failure result$1.class
│   │   │   │   │   │   │   │   ├── RemoteAuthDataSourceTest$register returns http error preserving code and error body$1.class
│   │   │   │   │   │   │   │   └── RemoteAuthDataSourceTest.class
│   │   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │   │       └── auth.kotlin_module
│   │   │   │   │   │   └── jars/
│   │   │   │   │   └── release/transformReleaseClassesWithAsm/
│   │   │   │   │       ├── dirs/
│   │   │   │   │       │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │       │   │   ├── di/
│   │   │   │   │       │   │   │   └── AuthModule.class
│   │   │   │   │       │   │   ├── internal/
│   │   │   │   │       │   │   │   ├── EncryptedTokenStorage$Companion.class
│   │   │   │   │       │   │   │   ├── EncryptedTokenStorage.class
│   │   │   │   │       │   │   │   ├── EncryptedTokenStorage_Factory.class
│   │   │   │   │       │   │   │   ├── FakeVaultSessionManager.class
│   │   │   │   │       │   │   │   ├── FakeVaultSessionManager_Factory$InstanceHolder.class
│   │   │   │   │       │   │   │   └── FakeVaultSessionManager_Factory.class
│   │   │   │   │       │   │   ├── AuthRepository.class
│   │   │   │   │       │   │   ├── SessionManager.class
│   │   │   │   │       │   │   ├── SessionManager_Factory.class
│   │   │   │   │       │   │   ├── SessionState$LoggedIn.class
│   │   │   │   │       │   │   ├── SessionState$LoggedOut.class
│   │   │   │   │       │   │   ├── SessionState.class
│   │   │   │   │       │   │   ├── TokenStorage.class
│   │   │   │   │       │   │   ├── VaultSessionManager.class
│   │   │   │   │       │   │   ├── VaultState$Locked.class
│   │   │   │   │       │   │   ├── VaultState$NotInitialized.class
│   │   │   │   │       │   │   ├── VaultState$Unknown.class
│   │   │   │   │       │   │   ├── VaultState$Unlocked.class
│   │   │   │   │       │   │   └── VaultState.class
│   │   │   │   │       │   ├── hilt_aggregated_deps/
│   │   │   │   │       │   │   └── _com_miguelrodriguez19_safecube_core_auth_di_AuthModule.class
│   │   │   │   │       │   └── META-INF/
│   │   │   │   │       │       └── auth.kotlin_module
│   │   │   │   │       └── jars/
│   │   │   │   ├── compile_and_runtime_r_class_jar/
│   │   │   │   │   ├── debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   └── debugUnitTest/generateDebugUnitTestStubRFile/
│   │   │   │   │       └── R.jar
│   │   │   │   ├── compile_library_classes_jar/
│   │   │   │   │   ├── debug/bundleLibCompileToJarDebug/
│   │   │   │   │   │   └── classes.jar
│   │   │   │   │   └── release/bundleLibCompileToJarRelease/
│   │   │   │   │       └── classes.jar
│   │   │   │   ├── compile_r_class_jar/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── R.jar
│   │   │   │   ├── compile_symbol_list/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── R.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── R.txt
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── R.txt
│   │   │   │   ├── compiled_local_resources/debug/compileDebugLibraryResources/out/
│   │   │   │   ├── compiled_navigation_res/debugAndroidTest/compileDebugAndroidTestNavigationResources/
│   │   │   │   ├── compressed_assets/debugAndroidTest/compressDebugAndroidTestAssets/out/assets/
│   │   │   │   │   └── PublicSuffixDatabase.list.jar
│   │   │   │   ├── consumer_proguard_dir/release/
│   │   │   │   ├── data_binding_layout_info_type_merge/debugAndroidTest/mergeDebugAndroidTestResources/out/
│   │   │   │   ├── data_binding_layout_info_type_package/
│   │   │   │   │   ├── debug/packageDebugResources/out/
│   │   │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/out/
│   │   │   │   │   └── release/packageReleaseResources/out/
│   │   │   │   ├── desugar_graph/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   │   ├── currentProject/
│   │   │   │   │   │   ├── jar_d0ce4da88813d3da166ec58353badf92a747b52ecf3723d015f0eb7aefaa0e49_bucket_0/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_d0ce4da88813d3da166ec58353badf92a747b52ecf3723d015f0eb7aefaa0e49_bucket_1/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_d0ce4da88813d3da166ec58353badf92a747b52ecf3723d015f0eb7aefaa0e49_bucket_2/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_d0ce4da88813d3da166ec58353badf92a747b52ecf3723d015f0eb7aefaa0e49_bucket_3/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_d0ce4da88813d3da166ec58353badf92a747b52ecf3723d015f0eb7aefaa0e49_bucket_4/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   └── jar_d0ce4da88813d3da166ec58353badf92a747b52ecf3723d015f0eb7aefaa0e49_bucket_5/
│   │   │   │   │   │       └── graph.bin
│   │   │   │   │   ├── externalLibs/
│   │   │   │   │   ├── mixedScopes/
│   │   │   │   │   └── otherProjects/
│   │   │   │   ├── dex/debugAndroidTest/
│   │   │   │   │   ├── mergeExtDexDebugAndroidTest/
│   │   │   │   │   │   └── classes.dex
│   │   │   │   │   ├── mergeLibDexDebugAndroidTest/
│   │   │   │   │   │   ├── 0/
│   │   │   │   │   │   ├── 1/
│   │   │   │   │   │   ├── 10/
│   │   │   │   │   │   ├── 11/
│   │   │   │   │   │   ├── 12/
│   │   │   │   │   │   ├── 13/
│   │   │   │   │   │   ├── 14/
│   │   │   │   │   │   ├── 15/
│   │   │   │   │   │   ├── 2/
│   │   │   │   │   │   ├── 3/
│   │   │   │   │   │   ├── 4/
│   │   │   │   │   │   ├── 5/
│   │   │   │   │   │   ├── 6/
│   │   │   │   │   │   ├── 7/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 8/
│   │   │   │   │   │   └── 9/
│   │   │   │   │   └── mergeProjectDexDebugAndroidTest/
│   │   │   │   │       ├── 0/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 1/
│   │   │   │   │       ├── 10/
│   │   │   │   │       ├── 11/
│   │   │   │   │       ├── 12/
│   │   │   │   │       ├── 13/
│   │   │   │   │       ├── 14/
│   │   │   │   │       ├── 15/
│   │   │   │   │       ├── 2/
│   │   │   │   │       ├── 3/
│   │   │   │   │       ├── 4/
│   │   │   │   │       ├── 5/
│   │   │   │   │       ├── 6/
│   │   │   │   │       ├── 7/
│   │   │   │   │       ├── 8/
│   │   │   │   │       └── 9/
│   │   │   │   ├── dex_archive_input_jar_hashes/debugAndroidTest/dexBuilderDebugAndroidTest/
│   │   │   │   │   └── out
│   │   │   │   ├── dex_number_of_buckets_file/debugAndroidTest/dexBuilderDebugAndroidTest/
│   │   │   │   │   └── out
│   │   │   │   ├── duplicate_classes_check/debugAndroidTest/checkDebugAndroidTestDuplicateClasses/
│   │   │   │   ├── external_file_lib_dex_archives/debugAndroidTest/desugarDebugAndroidTestFileDependencies/
│   │   │   │   ├── external_libs_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── external_libs_dex_archive_with_artifact_transforms/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── generated_proguard_file/
│   │   │   │   │   ├── debug/mergeDebugGeneratedProguardFiles/
│   │   │   │   │   └── release/mergeReleaseGeneratedProguardFiles/
│   │   │   │   ├── hilt/copy/debugUnitTest/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   └── AuthModule.class
│   │   │   │   │   │   ├── internal/
│   │   │   │   │   │   │   ├── EncryptedTokenStorage$Companion.class
│   │   │   │   │   │   │   ├── EncryptedTokenStorage.class
│   │   │   │   │   │   │   ├── EncryptedTokenStorage_Factory.class
│   │   │   │   │   │   │   ├── FakeVaultSessionManager.class
│   │   │   │   │   │   │   ├── FakeVaultSessionManager_Factory$InstanceHolder.class
│   │   │   │   │   │   │   └── FakeVaultSessionManager_Factory.class
│   │   │   │   │   │   ├── AuthError$AccountAlreadyExists.class
│   │   │   │   │   │   ├── AuthError$AccountNotActive.class
│   │   │   │   │   │   ├── AuthError$Conflict.class
│   │   │   │   │   │   ├── AuthError$Forbidden.class
│   │   │   │   │   │   ├── AuthError$InvalidCredentials.class
│   │   │   │   │   │   ├── AuthError$Unknown.class
│   │   │   │   │   │   ├── AuthError$ValidationFailed.class
│   │   │   │   │   │   ├── AuthError.class
│   │   │   │   │   │   ├── AuthErrorMapper$ParsedErrorBody.class
│   │   │   │   │   │   ├── AuthErrorMapper.class
│   │   │   │   │   │   ├── AuthErrorMapper_Factory$InstanceHolder.class
│   │   │   │   │   │   ├── AuthErrorMapper_Factory.class
│   │   │   │   │   │   ├── AuthErrorMapperTest.class
│   │   │   │   │   │   ├── AuthOperation.class
│   │   │   │   │   │   ├── AuthRepository$DefaultImpls.class
│   │   │   │   │   │   ├── AuthRepository.class
│   │   │   │   │   │   ├── AuthRepositoryFakeAuthControllerApi.class
│   │   │   │   │   │   ├── AuthRepositoryImpl$login$1.class
│   │   │   │   │   │   ├── AuthRepositoryImpl$logout$1.class
│   │   │   │   │   │   ├── AuthRepositoryImpl$refresh$1.class
│   │   │   │   │   │   ├── AuthRepositoryImpl$register$1.class
│   │   │   │   │   │   ├── AuthRepositoryImpl.class
│   │   │   │   │   │   ├── AuthRepositoryImpl_Factory.class
│   │   │   │   │   │   ├── AuthRepositoryImplTest$login maps successful response into domain auth tokens$1.class
│   │   │   │   │   │   ├── AuthRepositoryImplTest$logout maps transport failure into unknown auth error$1.class
│   │   │   │   │   │   ├── AuthRepositoryImplTest$refresh maps 401 into invalid credentials$1.class
│   │   │   │   │   │   ├── AuthRepositoryImplTest$register maps 409 into account already exists$1.class
│   │   │   │   │   │   ├── AuthRepositoryImplTest$register maps successful response into domain account$1.class
│   │   │   │   │   │   ├── AuthRepositoryImplTest.class
│   │   │   │   │   │   ├── AuthResult$Error.class
│   │   │   │   │   │   ├── AuthResult$Success.class
│   │   │   │   │   │   ├── AuthResult.class
│   │   │   │   │   │   ├── AuthTokens.class
│   │   │   │   │   │   ├── FakeAuthControllerApi.class
│   │   │   │   │   │   ├── NetworkResult$Failure.class
│   │   │   │   │   │   ├── NetworkResult$HttpError.class
│   │   │   │   │   │   ├── NetworkResult$Success.class
│   │   │   │   │   │   ├── NetworkResult.class
│   │   │   │   │   │   ├── RegisteredAccount.class
│   │   │   │   │   │   ├── RemoteAuthDataSource$execute$1.class
│   │   │   │   │   │   ├── RemoteAuthDataSource$login$2.class
│   │   │   │   │   │   ├── RemoteAuthDataSource$logout$2.class
│   │   │   │   │   │   ├── RemoteAuthDataSource$refresh$2.class
│   │   │   │   │   │   ├── RemoteAuthDataSource$register$2.class
│   │   │   │   │   │   ├── RemoteAuthDataSource.class
│   │   │   │   │   │   ├── RemoteAuthDataSource_Factory.class
│   │   │   │   │   │   ├── RemoteAuthDataSourceTest$login returns success result preserving code and body$1.class
│   │   │   │   │   │   ├── RemoteAuthDataSourceTest$refresh wraps transport exceptions into failure result$1.class
│   │   │   │   │   │   ├── RemoteAuthDataSourceTest$register returns http error preserving code and error body$1.class
│   │   │   │   │   │   ├── RemoteAuthDataSourceTest.class
│   │   │   │   │   │   ├── SessionManager.class
│   │   │   │   │   │   ├── SessionManager_Factory.class
│   │   │   │   │   │   ├── SessionState$LoggedIn.class
│   │   │   │   │   │   ├── SessionState$LoggedOut.class
│   │   │   │   │   │   ├── SessionState.class
│   │   │   │   │   │   ├── TokenStorage.class
│   │   │   │   │   │   ├── VaultSessionManager.class
│   │   │   │   │   │   ├── VaultState$Locked.class
│   │   │   │   │   │   ├── VaultState$NotInitialized.class
│   │   │   │   │   │   ├── VaultState$Unknown.class
│   │   │   │   │   │   ├── VaultState$Unlocked.class
│   │   │   │   │   │   └── VaultState.class
│   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_auth_di_AuthModule.class
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── auth.kotlin_module
│   │   │   │   ├── incremental/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── debug-mergeJavaRes/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   └── merge-state
│   │   │   │   │   ├── debugAndroidTest/
│   │   │   │   │   │   ├── mergeDebugAndroidTestResources/
│   │   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   │   │   ├── values/
│   │   │   │   │   │   │   │   │   └── values.xml
│   │   │   │   │   │   │   │   ├── values-af/
│   │   │   │   │   │   │   │   │   └── values-af.xml
│   │   │   │   │   │   │   │   ├── values-am/
│   │   │   │   │   │   │   │   │   └── values-am.xml
│   │   │   │   │   │   │   │   ├── values-ar/
│   │   │   │   │   │   │   │   │   └── values-ar.xml
│   │   │   │   │   │   │   │   ├── values-as/
│   │   │   │   │   │   │   │   │   └── values-as.xml
│   │   │   │   │   │   │   │   ├── values-az/
│   │   │   │   │   │   │   │   │   └── values-az.xml
│   │   │   │   │   │   │   │   ├── values-b+sr+Latn/
│   │   │   │   │   │   │   │   │   └── values-b+sr+Latn.xml
│   │   │   │   │   │   │   │   ├── values-be/
│   │   │   │   │   │   │   │   │   └── values-be.xml
│   │   │   │   │   │   │   │   ├── values-bg/
│   │   │   │   │   │   │   │   │   └── values-bg.xml
│   │   │   │   │   │   │   │   ├── values-bn/
│   │   │   │   │   │   │   │   │   └── values-bn.xml
│   │   │   │   │   │   │   │   ├── values-bs/
│   │   │   │   │   │   │   │   │   └── values-bs.xml
│   │   │   │   │   │   │   │   ├── values-ca/
│   │   │   │   │   │   │   │   │   └── values-ca.xml
│   │   │   │   │   │   │   │   ├── values-cs/
│   │   │   │   │   │   │   │   │   └── values-cs.xml
│   │   │   │   │   │   │   │   ├── values-da/
│   │   │   │   │   │   │   │   │   └── values-da.xml
│   │   │   │   │   │   │   │   ├── values-de/
│   │   │   │   │   │   │   │   │   └── values-de.xml
│   │   │   │   │   │   │   │   ├── values-el/
│   │   │   │   │   │   │   │   │   └── values-el.xml
│   │   │   │   │   │   │   │   ├── values-en-rAU/
│   │   │   │   │   │   │   │   │   └── values-en-rAU.xml
│   │   │   │   │   │   │   │   ├── values-en-rCA/
│   │   │   │   │   │   │   │   │   └── values-en-rCA.xml
│   │   │   │   │   │   │   │   ├── values-en-rGB/
│   │   │   │   │   │   │   │   │   └── values-en-rGB.xml
│   │   │   │   │   │   │   │   ├── values-en-rIN/
│   │   │   │   │   │   │   │   │   └── values-en-rIN.xml
│   │   │   │   │   │   │   │   ├── values-en-rXC/
│   │   │   │   │   │   │   │   │   └── values-en-rXC.xml
│   │   │   │   │   │   │   │   ├── values-es/
│   │   │   │   │   │   │   │   │   └── values-es.xml
│   │   │   │   │   │   │   │   ├── values-es-rUS/
│   │   │   │   │   │   │   │   │   └── values-es-rUS.xml
│   │   │   │   │   │   │   │   ├── values-et/
│   │   │   │   │   │   │   │   │   └── values-et.xml
│   │   │   │   │   │   │   │   ├── values-eu/
│   │   │   │   │   │   │   │   │   └── values-eu.xml
│   │   │   │   │   │   │   │   ├── values-fa/
│   │   │   │   │   │   │   │   │   └── values-fa.xml
│   │   │   │   │   │   │   │   ├── values-fi/
│   │   │   │   │   │   │   │   │   └── values-fi.xml
│   │   │   │   │   │   │   │   ├── values-fr/
│   │   │   │   │   │   │   │   │   └── values-fr.xml
│   │   │   │   │   │   │   │   ├── values-fr-rCA/
│   │   │   │   │   │   │   │   │   └── values-fr-rCA.xml
│   │   │   │   │   │   │   │   ├── values-gl/
│   │   │   │   │   │   │   │   │   └── values-gl.xml
│   │   │   │   │   │   │   │   ├── values-gu/
│   │   │   │   │   │   │   │   │   └── values-gu.xml
│   │   │   │   │   │   │   │   ├── values-hi/
│   │   │   │   │   │   │   │   │   └── values-hi.xml
│   │   │   │   │   │   │   │   ├── values-hr/
│   │   │   │   │   │   │   │   │   └── values-hr.xml
│   │   │   │   │   │   │   │   ├── values-hu/
│   │   │   │   │   │   │   │   │   └── values-hu.xml
│   │   │   │   │   │   │   │   ├── values-hy/
│   │   │   │   │   │   │   │   │   └── values-hy.xml
│   │   │   │   │   │   │   │   ├── values-in/
│   │   │   │   │   │   │   │   │   └── values-in.xml
│   │   │   │   │   │   │   │   ├── values-is/
│   │   │   │   │   │   │   │   │   └── values-is.xml
│   │   │   │   │   │   │   │   ├── values-it/
│   │   │   │   │   │   │   │   │   └── values-it.xml
│   │   │   │   │   │   │   │   ├── values-iw/
│   │   │   │   │   │   │   │   │   └── values-iw.xml
│   │   │   │   │   │   │   │   ├── values-ja/
│   │   │   │   │   │   │   │   │   └── values-ja.xml
│   │   │   │   │   │   │   │   ├── values-ka/
│   │   │   │   │   │   │   │   │   └── values-ka.xml
│   │   │   │   │   │   │   │   ├── values-kk/
│   │   │   │   │   │   │   │   │   └── values-kk.xml
│   │   │   │   │   │   │   │   ├── values-km/
│   │   │   │   │   │   │   │   │   └── values-km.xml
│   │   │   │   │   │   │   │   ├── values-kn/
│   │   │   │   │   │   │   │   │   └── values-kn.xml
│   │   │   │   │   │   │   │   ├── values-ko/
│   │   │   │   │   │   │   │   │   └── values-ko.xml
│   │   │   │   │   │   │   │   ├── values-ky/
│   │   │   │   │   │   │   │   │   └── values-ky.xml
│   │   │   │   │   │   │   │   ├── values-lo/
│   │   │   │   │   │   │   │   │   └── values-lo.xml
│   │   │   │   │   │   │   │   ├── values-lt/
│   │   │   │   │   │   │   │   │   └── values-lt.xml
│   │   │   │   │   │   │   │   ├── values-lv/
│   │   │   │   │   │   │   │   │   └── values-lv.xml
│   │   │   │   │   │   │   │   ├── values-mk/
│   │   │   │   │   │   │   │   │   └── values-mk.xml
│   │   │   │   │   │   │   │   ├── values-ml/
│   │   │   │   │   │   │   │   │   └── values-ml.xml
│   │   │   │   │   │   │   │   ├── values-mn/
│   │   │   │   │   │   │   │   │   └── values-mn.xml
│   │   │   │   │   │   │   │   ├── values-mr/
│   │   │   │   │   │   │   │   │   └── values-mr.xml
│   │   │   │   │   │   │   │   ├── values-ms/
│   │   │   │   │   │   │   │   │   └── values-ms.xml
│   │   │   │   │   │   │   │   ├── values-my/
│   │   │   │   │   │   │   │   │   └── values-my.xml
│   │   │   │   │   │   │   │   ├── values-nb/
│   │   │   │   │   │   │   │   │   └── values-nb.xml
│   │   │   │   │   │   │   │   ├── values-ne/
│   │   │   │   │   │   │   │   │   └── values-ne.xml
│   │   │   │   │   │   │   │   ├── values-nl/
│   │   │   │   │   │   │   │   │   └── values-nl.xml
│   │   │   │   │   │   │   │   ├── values-or/
│   │   │   │   │   │   │   │   │   └── values-or.xml
│   │   │   │   │   │   │   │   ├── values-pa/
│   │   │   │   │   │   │   │   │   └── values-pa.xml
│   │   │   │   │   │   │   │   ├── values-pl/
│   │   │   │   │   │   │   │   │   └── values-pl.xml
│   │   │   │   │   │   │   │   ├── values-pt/
│   │   │   │   │   │   │   │   │   └── values-pt.xml
│   │   │   │   │   │   │   │   ├── values-pt-rBR/
│   │   │   │   │   │   │   │   │   └── values-pt-rBR.xml
│   │   │   │   │   │   │   │   ├── values-pt-rPT/
│   │   │   │   │   │   │   │   │   └── values-pt-rPT.xml
│   │   │   │   │   │   │   │   ├── values-ro/
│   │   │   │   │   │   │   │   │   └── values-ro.xml
│   │   │   │   │   │   │   │   ├── values-ru/
│   │   │   │   │   │   │   │   │   └── values-ru.xml
│   │   │   │   │   │   │   │   ├── values-si/
│   │   │   │   │   │   │   │   │   └── values-si.xml
│   │   │   │   │   │   │   │   ├── values-sk/
│   │   │   │   │   │   │   │   │   └── values-sk.xml
│   │   │   │   │   │   │   │   ├── values-sl/
│   │   │   │   │   │   │   │   │   └── values-sl.xml
│   │   │   │   │   │   │   │   ├── values-sq/
│   │   │   │   │   │   │   │   │   └── values-sq.xml
│   │   │   │   │   │   │   │   ├── values-sr/
│   │   │   │   │   │   │   │   │   └── values-sr.xml
│   │   │   │   │   │   │   │   ├── values-sv/
│   │   │   │   │   │   │   │   │   └── values-sv.xml
│   │   │   │   │   │   │   │   ├── values-sw/
│   │   │   │   │   │   │   │   │   └── values-sw.xml
│   │   │   │   │   │   │   │   ├── values-ta/
│   │   │   │   │   │   │   │   │   └── values-ta.xml
│   │   │   │   │   │   │   │   ├── values-te/
│   │   │   │   │   │   │   │   │   └── values-te.xml
│   │   │   │   │   │   │   │   ├── values-th/
│   │   │   │   │   │   │   │   │   └── values-th.xml
│   │   │   │   │   │   │   │   ├── values-tl/
│   │   │   │   │   │   │   │   │   └── values-tl.xml
│   │   │   │   │   │   │   │   ├── values-tr/
│   │   │   │   │   │   │   │   │   └── values-tr.xml
│   │   │   │   │   │   │   │   ├── values-uk/
│   │   │   │   │   │   │   │   │   └── values-uk.xml
│   │   │   │   │   │   │   │   ├── values-ur/
│   │   │   │   │   │   │   │   │   └── values-ur.xml
│   │   │   │   │   │   │   │   ├── values-uz/
│   │   │   │   │   │   │   │   │   └── values-uz.xml
│   │   │   │   │   │   │   │   ├── values-v21/
│   │   │   │   │   │   │   │   │   └── values-v21.xml
│   │   │   │   │   │   │   │   ├── values-vi/
│   │   │   │   │   │   │   │   │   └── values-vi.xml
│   │   │   │   │   │   │   │   ├── values-zh-rCN/
│   │   │   │   │   │   │   │   │   └── values-zh-rCN.xml
│   │   │   │   │   │   │   │   ├── values-zh-rHK/
│   │   │   │   │   │   │   │   │   └── values-zh-rHK.xml
│   │   │   │   │   │   │   │   ├── values-zh-rTW/
│   │   │   │   │   │   │   │   │   └── values-zh-rTW.xml
│   │   │   │   │   │   │   │   └── values-zu/
│   │   │   │   │   │   │   │       └── values-zu.xml
│   │   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   │   └── packageDebugAndroidTestResources/
│   │   │   │   │   │       ├── merged.dir/
│   │   │   │   │   │       ├── stripped.dir/
│   │   │   │   │   │       │   │   │   │   │   │       └── merger.xml
│   │   │   │   │   ├── debugAndroidTest-mergeJavaRes/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   │   ├── 1ZuCeLYWmYw5MfiXYYnDIw==
│   │   │   │   │   │   │   ├── 3VJ0q7BGqqlcLs7MI_dHlw==
│   │   │   │   │   │   │   ├── 7trlVWptO4XEuV+Pwn+FJg==
│   │   │   │   │   │   │   ├── _hdAXZS0wZVQGsNJYelpuw==
│   │   │   │   │   │   │   ├── Aum8RyvyKLwxGsZvF8wZew==
│   │   │   │   │   │   │   ├── B081BuIDaWSo5McNdeDOlg==
│   │   │   │   │   │   │   ├── BIO8B+IR6k405DzX3KVLfg==
│   │   │   │   │   │   │   ├── eAb6b17AoioR+wuvsUlXUg==
│   │   │   │   │   │   │   ├── EKfNJDNp1qej0g_hpuBvyw==
│   │   │   │   │   │   │   ├── fbJN1jTpNAQTBDjunGpNNw==
│   │   │   │   │   │   │   ├── gMUj5N1rwbVTZNnhBOwS4w==
│   │   │   │   │   │   │   ├── i1fywGgR7739mcd2F7zSGg==
│   │   │   │   │   │   │   ├── IO63ZVBDFar17KQf_eKhoQ==
│   │   │   │   │   │   │   ├── J7CAnpdxVb_3lbUfaCbsMg==
│   │   │   │   │   │   │   ├── J7iYjow924XXI0QA2R4XxA==
│   │   │   │   │   │   │   ├── jrJKw3qGpVbyDMBUg69i1A==
│   │   │   │   │   │   │   ├── kBPjyU0m7mBApb7pIiwI2Q==
│   │   │   │   │   │   │   ├── Kws7Ph2jc1f3d_ecLLw8yA==
│   │   │   │   │   │   │   ├── L+aTIiZM60Y12ESzMr+bBQ==
│   │   │   │   │   │   │   ├── m7ifgdNNq+vMZDPtmarc1Q==
│   │   │   │   │   │   │   ├── MGjkoJZVhVbQnzYuPyFT2g==
│   │   │   │   │   │   │   ├── MoWJlOGWBfjVRC8RvC2PxA==
│   │   │   │   │   │   │   ├── N4JbPtX00mbgf1JBj8hLpA==
│   │   │   │   │   │   │   ├── qD5dgASdHa8TAOro2Bvrtw==
│   │   │   │   │   │   │   ├── qOLoLN95hFQC6sku5Qy+CQ==
│   │   │   │   │   │   │   ├── rrna0K8qZsqmJGvstjZKCQ==
│   │   │   │   │   │   │   ├── tL2pp5i9_KZCC65aeIVhDg==
│   │   │   │   │   │   │   ├── v1FFNCoEO5or6_tvb1ZiNA==
│   │   │   │   │   │   │   └── V8DxNbbYWglX3HsdJ5bXKg==
│   │   │   │   │   │   └── merge-state
│   │   │   │   │   ├── mergeDebugAndroidTestAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugAndroidTestJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeReleaseAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeReleaseJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── packageDebugAndroidTest/tmp/debugAndroidTest/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   │   ├── androidResources
│   │   │   │   │   │   │   └── javaResources0
│   │   │   │   │   │   └── dex-renamer-state.txt
│   │   │   │   │   ├── release/packageReleaseResources/
│   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── transformDebugClassesWithAsm/
│   │   │   │   │   ├── transformDebugUnitTestClassesWithAsm/
│   │   │   │   │   └── transformReleaseClassesWithAsm/
│   │   │   │   ├── java_res/
│   │   │   │   │   ├── debug/processDebugJavaRes/out/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   └── internal/
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── auth.kotlin_module
│   │   │   │   │   ├── debugUnitTest/processDebugUnitTestJavaRes/out/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── auth.kotlin_module
│   │   │   │   │   └── release/processReleaseJavaRes/out/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │       │   ├── di/
│   │   │   │   │       │   └── internal/
│   │   │   │   │       └── META-INF/
│   │   │   │   │           └── auth.kotlin_module
│   │   │   │   ├── javac/
│   │   │   │   │   ├── debug/compileDebugJavaWithJavac/classes/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   │   │   ├── internal/
│   │   │   │   │   │   │   │   ├── EncryptedTokenStorage_Factory.class
│   │   │   │   │   │   │   │   ├── FakeVaultSessionManager_Factory$InstanceHolder.class
│   │   │   │   │   │   │   │   └── FakeVaultSessionManager_Factory.class
│   │   │   │   │   │   │   ├── AuthErrorMapper_Factory$InstanceHolder.class
│   │   │   │   │   │   │   ├── AuthErrorMapper_Factory.class
│   │   │   │   │   │   │   ├── AuthRepositoryImpl_Factory.class
│   │   │   │   │   │   │   ├── RemoteAuthDataSource_Factory.class
│   │   │   │   │   │   │   └── SessionManager_Factory.class
│   │   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_core_auth_di_AuthModule.class
│   │   │   │   │   └── release/compileReleaseJavaWithJavac/classes/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │       │   ├── internal/
│   │   │   │   │       │   │   ├── EncryptedTokenStorage_Factory.class
│   │   │   │   │       │   │   ├── FakeVaultSessionManager_Factory$InstanceHolder.class
│   │   │   │   │       │   │   └── FakeVaultSessionManager_Factory.class
│   │   │   │   │       │   └── SessionManager_Factory.class
│   │   │   │   │       └── hilt_aggregated_deps/
│   │   │   │   │           └── _com_miguelrodriguez19_safecube_core_auth_di_AuthModule.class
│   │   │   │   ├── library_and_local_jars_jni/debug/copyDebugJniLibsProjectAndLocalJars/jni/
│   │   │   │   ├── library_art_profile/
│   │   │   │   │   ├── debug/prepareDebugArtProfile/
│   │   │   │   │   └── release/prepareReleaseArtProfile/
│   │   │   │   ├── library_jni/
│   │   │   │   │   ├── debug/copyDebugJniLibsProjectOnly/jni/
│   │   │   │   │   └── release/copyReleaseJniLibsProjectOnly/jni/
│   │   │   │   ├── linked_resources_binary_format/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   ├── linked-resources-binary-format.ap_
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── lint_publish_jar/global/prepareLintJarForPublish/
│   │   │   │   ├── local_only_symbol_list/
│   │   │   │   │   ├── debug/parseDebugLocalResources/
│   │   │   │   │   │   └── R-def.txt
│   │   │   │   │   ├── debugAndroidTest/parseDebugAndroidTestLocalResources/
│   │   │   │   │   │   └── R-def.txt
│   │   │   │   │   └── release/parseReleaseLocalResources/
│   │   │   │   │       └── R-def.txt
│   │   │   │   ├── manifest_merge_blame_file/
│   │   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   │   └── manifest-merger-blame-debug-report.txt
│   │   │   │   │   ├── debugAndroidTest/processDebugAndroidTestManifest/
│   │   │   │   │   │   └── manifest-merger-blame-debug-androidTest-report.txt
│   │   │   │   │   └── release/processReleaseManifest/
│   │   │   │   │       └── manifest-merger-blame-release-report.txt
│   │   │   │   ├── merged_consumer_proguard_file/debug/mergeDebugConsumerProguardFiles/
│   │   │   │   ├── merged_java_res/
│   │   │   │   │   ├── debug/mergeDebugJavaResource/
│   │   │   │   │   │   └── feature-auth.jar
│   │   │   │   │   └── debugAndroidTest/mergeDebugAndroidTestJavaResource/
│   │   │   │   │       └── feature-auth.jar
│   │   │   │   ├── merged_jni_libs/
│   │   │   │   │   ├── debug/mergeDebugJniLibFolders/out/
│   │   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestJniLibFolders/out/
│   │   │   │   │   └── release/mergeReleaseJniLibFolders/out/
│   │   │   │   ├── merged_manifest/
│   │   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   │   └── AndroidManifest.xml
│   │   │   │   │   └── release/processReleaseManifest/
│   │   │   │   │       └── AndroidManifest.xml
│   │   │   │   ├── merged_res/debugAndroidTest/mergeDebugAndroidTestResources/
│   │   │   │   │   ├── values-af_values-af.arsc.flat
│   │   │   │   │   ├── values-am_values-am.arsc.flat
│   │   │   │   │   ├── values-ar_values-ar.arsc.flat
│   │   │   │   │   ├── values-as_values-as.arsc.flat
│   │   │   │   │   ├── values-az_values-az.arsc.flat
│   │   │   │   │   ├── values-b+sr+Latn_values-b+sr+Latn.arsc.flat
│   │   │   │   │   ├── values-be_values-be.arsc.flat
│   │   │   │   │   ├── values-bg_values-bg.arsc.flat
│   │   │   │   │   ├── values-bn_values-bn.arsc.flat
│   │   │   │   │   ├── values-bs_values-bs.arsc.flat
│   │   │   │   │   ├── values-ca_values-ca.arsc.flat
│   │   │   │   │   ├── values-cs_values-cs.arsc.flat
│   │   │   │   │   ├── values-da_values-da.arsc.flat
│   │   │   │   │   ├── values-de_values-de.arsc.flat
│   │   │   │   │   ├── values-el_values-el.arsc.flat
│   │   │   │   │   ├── values-en-rAU_values-en-rAU.arsc.flat
│   │   │   │   │   ├── values-en-rCA_values-en-rCA.arsc.flat
│   │   │   │   │   ├── values-en-rGB_values-en-rGB.arsc.flat
│   │   │   │   │   ├── values-en-rIN_values-en-rIN.arsc.flat
│   │   │   │   │   ├── values-en-rXC_values-en-rXC.arsc.flat
│   │   │   │   │   ├── values-es-rUS_values-es-rUS.arsc.flat
│   │   │   │   │   ├── values-es_values-es.arsc.flat
│   │   │   │   │   ├── values-et_values-et.arsc.flat
│   │   │   │   │   ├── values-eu_values-eu.arsc.flat
│   │   │   │   │   ├── values-fa_values-fa.arsc.flat
│   │   │   │   │   ├── values-fi_values-fi.arsc.flat
│   │   │   │   │   ├── values-fr-rCA_values-fr-rCA.arsc.flat
│   │   │   │   │   ├── values-fr_values-fr.arsc.flat
│   │   │   │   │   ├── values-gl_values-gl.arsc.flat
│   │   │   │   │   ├── values-gu_values-gu.arsc.flat
│   │   │   │   │   ├── values-hi_values-hi.arsc.flat
│   │   │   │   │   ├── values-hr_values-hr.arsc.flat
│   │   │   │   │   ├── values-hu_values-hu.arsc.flat
│   │   │   │   │   ├── values-hy_values-hy.arsc.flat
│   │   │   │   │   ├── values-in_values-in.arsc.flat
│   │   │   │   │   ├── values-is_values-is.arsc.flat
│   │   │   │   │   ├── values-it_values-it.arsc.flat
│   │   │   │   │   ├── values-iw_values-iw.arsc.flat
│   │   │   │   │   ├── values-ja_values-ja.arsc.flat
│   │   │   │   │   ├── values-ka_values-ka.arsc.flat
│   │   │   │   │   ├── values-kk_values-kk.arsc.flat
│   │   │   │   │   ├── values-km_values-km.arsc.flat
│   │   │   │   │   ├── values-kn_values-kn.arsc.flat
│   │   │   │   │   ├── values-ko_values-ko.arsc.flat
│   │   │   │   │   ├── values-ky_values-ky.arsc.flat
│   │   │   │   │   ├── values-lo_values-lo.arsc.flat
│   │   │   │   │   ├── values-lt_values-lt.arsc.flat
│   │   │   │   │   ├── values-lv_values-lv.arsc.flat
│   │   │   │   │   ├── values-mk_values-mk.arsc.flat
│   │   │   │   │   ├── values-ml_values-ml.arsc.flat
│   │   │   │   │   ├── values-mn_values-mn.arsc.flat
│   │   │   │   │   ├── values-mr_values-mr.arsc.flat
│   │   │   │   │   ├── values-ms_values-ms.arsc.flat
│   │   │   │   │   ├── values-my_values-my.arsc.flat
│   │   │   │   │   ├── values-nb_values-nb.arsc.flat
│   │   │   │   │   ├── values-ne_values-ne.arsc.flat
│   │   │   │   │   ├── values-nl_values-nl.arsc.flat
│   │   │   │   │   ├── values-or_values-or.arsc.flat
│   │   │   │   │   ├── values-pa_values-pa.arsc.flat
│   │   │   │   │   ├── values-pl_values-pl.arsc.flat
│   │   │   │   │   ├── values-pt-rBR_values-pt-rBR.arsc.flat
│   │   │   │   │   ├── values-pt-rPT_values-pt-rPT.arsc.flat
│   │   │   │   │   ├── values-pt_values-pt.arsc.flat
│   │   │   │   │   ├── values-ro_values-ro.arsc.flat
│   │   │   │   │   ├── values-ru_values-ru.arsc.flat
│   │   │   │   │   ├── values-si_values-si.arsc.flat
│   │   │   │   │   ├── values-sk_values-sk.arsc.flat
│   │   │   │   │   ├── values-sl_values-sl.arsc.flat
│   │   │   │   │   ├── values-sq_values-sq.arsc.flat
│   │   │   │   │   ├── values-sr_values-sr.arsc.flat
│   │   │   │   │   ├── values-sv_values-sv.arsc.flat
│   │   │   │   │   ├── values-sw_values-sw.arsc.flat
│   │   │   │   │   ├── values-ta_values-ta.arsc.flat
│   │   │   │   │   ├── values-te_values-te.arsc.flat
│   │   │   │   │   ├── values-th_values-th.arsc.flat
│   │   │   │   │   ├── values-tl_values-tl.arsc.flat
│   │   │   │   │   ├── values-tr_values-tr.arsc.flat
│   │   │   │   │   ├── values-uk_values-uk.arsc.flat
│   │   │   │   │   ├── values-ur_values-ur.arsc.flat
│   │   │   │   │   ├── values-uz_values-uz.arsc.flat
│   │   │   │   │   ├── values-v21_values-v21.arsc.flat
│   │   │   │   │   ├── values-vi_values-vi.arsc.flat
│   │   │   │   │   ├── values-zh-rCN_values-zh-rCN.arsc.flat
│   │   │   │   │   ├── values-zh-rHK_values-zh-rHK.arsc.flat
│   │   │   │   │   ├── values-zh-rTW_values-zh-rTW.arsc.flat
│   │   │   │   │   ├── values-zu_values-zu.arsc.flat
│   │   │   │   │   └── values_values.arsc.flat
│   │   │   │   ├── merged_res_blame_folder/debugAndroidTest/mergeDebugAndroidTestResources/out/multi-v2/
│   │   │   │   │   ├── mergeDebugAndroidTestResources.json
│   │   │   │   │   ├── values-af.json
│   │   │   │   │   ├── values-am.json
│   │   │   │   │   ├── values-ar.json
│   │   │   │   │   ├── values-as.json
│   │   │   │   │   ├── values-az.json
│   │   │   │   │   ├── values-b+sr+Latn.json
│   │   │   │   │   ├── values-be.json
│   │   │   │   │   ├── values-bg.json
│   │   │   │   │   ├── values-bn.json
│   │   │   │   │   ├── values-bs.json
│   │   │   │   │   ├── values-ca.json
│   │   │   │   │   ├── values-cs.json
│   │   │   │   │   ├── values-da.json
│   │   │   │   │   ├── values-de.json
│   │   │   │   │   ├── values-el.json
│   │   │   │   │   ├── values-en-rAU.json
│   │   │   │   │   ├── values-en-rCA.json
│   │   │   │   │   ├── values-en-rGB.json
│   │   │   │   │   ├── values-en-rIN.json
│   │   │   │   │   ├── values-en-rXC.json
│   │   │   │   │   ├── values-es-rUS.json
│   │   │   │   │   ├── values-es.json
│   │   │   │   │   ├── values-et.json
│   │   │   │   │   ├── values-eu.json
│   │   │   │   │   ├── values-fa.json
│   │   │   │   │   ├── values-fi.json
│   │   │   │   │   ├── values-fr-rCA.json
│   │   │   │   │   ├── values-fr.json
│   │   │   │   │   ├── values-gl.json
│   │   │   │   │   ├── values-gu.json
│   │   │   │   │   ├── values-hi.json
│   │   │   │   │   ├── values-hr.json
│   │   │   │   │   ├── values-hu.json
│   │   │   │   │   ├── values-hy.json
│   │   │   │   │   ├── values-in.json
│   │   │   │   │   ├── values-is.json
│   │   │   │   │   ├── values-it.json
│   │   │   │   │   ├── values-iw.json
│   │   │   │   │   ├── values-ja.json
│   │   │   │   │   ├── values-ka.json
│   │   │   │   │   ├── values-kk.json
│   │   │   │   │   ├── values-km.json
│   │   │   │   │   ├── values-kn.json
│   │   │   │   │   ├── values-ko.json
│   │   │   │   │   ├── values-ky.json
│   │   │   │   │   ├── values-lo.json
│   │   │   │   │   ├── values-lt.json
│   │   │   │   │   ├── values-lv.json
│   │   │   │   │   ├── values-mk.json
│   │   │   │   │   ├── values-ml.json
│   │   │   │   │   ├── values-mn.json
│   │   │   │   │   ├── values-mr.json
│   │   │   │   │   ├── values-ms.json
│   │   │   │   │   ├── values-my.json
│   │   │   │   │   ├── values-nb.json
│   │   │   │   │   ├── values-ne.json
│   │   │   │   │   ├── values-nl.json
│   │   │   │   │   ├── values-or.json
│   │   │   │   │   ├── values-pa.json
│   │   │   │   │   ├── values-pl.json
│   │   │   │   │   ├── values-pt-rBR.json
│   │   │   │   │   ├── values-pt-rPT.json
│   │   │   │   │   ├── values-pt.json
│   │   │   │   │   ├── values-ro.json
│   │   │   │   │   ├── values-ru.json
│   │   │   │   │   ├── values-si.json
│   │   │   │   │   ├── values-sk.json
│   │   │   │   │   ├── values-sl.json
│   │   │   │   │   ├── values-sq.json
│   │   │   │   │   ├── values-sr.json
│   │   │   │   │   ├── values-sv.json
│   │   │   │   │   ├── values-sw.json
│   │   │   │   │   ├── values-ta.json
│   │   │   │   │   ├── values-te.json
│   │   │   │   │   ├── values-th.json
│   │   │   │   │   ├── values-tl.json
│   │   │   │   │   ├── values-tr.json
│   │   │   │   │   ├── values-uk.json
│   │   │   │   │   ├── values-ur.json
│   │   │   │   │   ├── values-uz.json
│   │   │   │   │   ├── values-v21.json
│   │   │   │   │   ├── values-vi.json
│   │   │   │   │   ├── values-zh-rCN.json
│   │   │   │   │   ├── values-zh-rHK.json
│   │   │   │   │   ├── values-zh-rTW.json
│   │   │   │   │   ├── values-zu.json
│   │   │   │   │   └── values.json
│   │   │   │   ├── mixed_scope_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── navigation_json/
│   │   │   │   │   ├── debug/extractDeepLinksDebug/
│   │   │   │   │   │   └── navigation.json
│   │   │   │   │   └── release/extractDeepLinksRelease/
│   │   │   │   │       └── navigation.json
│   │   │   │   ├── navigation_json_for_aar/debug/extractDeepLinksForAarDebug/
│   │   │   │   ├── nested_resources_validation_report/
│   │   │   │   │   ├── debug/generateDebugResources/
│   │   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestResources/
│   │   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   │   └── release/generateReleaseResources/
│   │   │   │   │       └── nestedResourcesValidationReport.txt
│   │   │   │   ├── packaged_manifests/debugAndroidTest/processDebugAndroidTestManifest/
│   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── packaged_res/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   ├── project_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   │   ├── 30cbb43c8989284964cdde3e975d004f76da2cecd61bff04a336d800667127c5_0.jar
│   │   │   │   │   ├── 30cbb43c8989284964cdde3e975d004f76da2cecd61bff04a336d800667127c5_1.jar
│   │   │   │   │   ├── 30cbb43c8989284964cdde3e975d004f76da2cecd61bff04a336d800667127c5_2.jar
│   │   │   │   │   ├── 30cbb43c8989284964cdde3e975d004f76da2cecd61bff04a336d800667127c5_3.jar
│   │   │   │   │   ├── 30cbb43c8989284964cdde3e975d004f76da2cecd61bff04a336d800667127c5_4.jar
│   │   │   │   │   └── 30cbb43c8989284964cdde3e975d004f76da2cecd61bff04a336d800667127c5_5.jar
│   │   │   │   ├── public_res/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   ├── runtime_library_classes_dir/debug/bundleLibRuntimeToDirDebug/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   └── AuthModule.class
│   │   │   │   │   │   ├── internal/
│   │   │   │   │   │   │   ├── EncryptedTokenStorage$Companion.class
│   │   │   │   │   │   │   ├── EncryptedTokenStorage.class
│   │   │   │   │   │   │   ├── EncryptedTokenStorage_Factory.class
│   │   │   │   │   │   │   ├── FakeVaultSessionManager.class
│   │   │   │   │   │   │   ├── FakeVaultSessionManager_Factory$InstanceHolder.class
│   │   │   │   │   │   │   └── FakeVaultSessionManager_Factory.class
│   │   │   │   │   │   ├── AuthError$AccountAlreadyExists.class
│   │   │   │   │   │   ├── AuthError$AccountNotActive.class
│   │   │   │   │   │   ├── AuthError$Conflict.class
│   │   │   │   │   │   ├── AuthError$Forbidden.class
│   │   │   │   │   │   ├── AuthError$InvalidCredentials.class
│   │   │   │   │   │   ├── AuthError$Unknown.class
│   │   │   │   │   │   ├── AuthError$ValidationFailed.class
│   │   │   │   │   │   ├── AuthError.class
│   │   │   │   │   │   ├── AuthErrorMapper$ParsedErrorBody.class
│   │   │   │   │   │   ├── AuthErrorMapper.class
│   │   │   │   │   │   ├── AuthErrorMapper_Factory$InstanceHolder.class
│   │   │   │   │   │   ├── AuthErrorMapper_Factory.class
│   │   │   │   │   │   ├── AuthOperation.class
│   │   │   │   │   │   ├── AuthRepository.class
│   │   │   │   │   │   ├── SessionManager.class
│   │   │   │   │   │   ├── SessionManager_Factory.class
│   │   │   │   │   │   ├── SessionState$LoggedIn.class
│   │   │   │   │   │   ├── SessionState$LoggedOut.class
│   │   │   │   │   │   ├── SessionState.class
│   │   │   │   │   │   ├── TokenStorage.class
│   │   │   │   │   │   ├── VaultSessionManager.class
│   │   │   │   │   │   ├── VaultState$Locked.class
│   │   │   │   │   │   ├── VaultState$NotInitialized.class
│   │   │   │   │   │   ├── VaultState$Unknown.class
│   │   │   │   │   │   ├── VaultState$Unlocked.class
│   │   │   │   │   │   └── VaultState.class
│   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_auth_di_AuthModule.class
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── auth.kotlin_module
│   │   │   │   ├── runtime_library_classes_jar/
│   │   │   │   │   ├── debug/bundleLibRuntimeToJarDebug/
│   │   │   │   │   │   └── classes.jar
│   │   │   │   │   └── release/bundleLibRuntimeToJarRelease/
│   │   │   │   │       └── classes.jar
│   │   │   │   ├── runtime_symbol_list/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── R.txt
│   │   │   │   ├── signing_config_versions/debugAndroidTest/writeDebugAndroidTestSigningConfigVersions/
│   │   │   │   │   └── signing-config-versions.json
│   │   │   │   ├── stable_resource_ids_file/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── stableIds.txt
│   │   │   │   ├── sub_project_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── symbol_list_with_package_name/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── package-aware-r.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── package-aware-r.txt
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── package-aware-r.txt
│   │   │   │   ├── tmp/manifest/androidTest/debug/
│   │   │   │   └── validate_signing_config/debugAndroidTest/validateSigningDebugAndroidTest/
│   │   │   ├── kotlin/
│   │   │   │   ├── compileDebugKotlin/
│   │   │   │   │   ├── cacheable/
│   │   │   │   │   │   ├── caches-jvm/
│   │   │   │   │   │   │   ├── compilerPluginFiles/
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │   │   │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │   │   │   │   ├── inputs/
│   │   │   │   │   │   │   │   ├── source-to-output.tab
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.values.at
│   │   │   │   │   │   │   │   ├── source-to-output.tab_i
│   │   │   │   │   │   │   │   └── source-to-output.tab_i.len
│   │   │   │   │   │   │   ├── jvm/
│   │   │   │   │   │   │   │   └── kotlin/
│   │   │   │   │   │   │   │       ├── class-attributes.tab
│   │   │   │   │   │   │   │       ├── class-attributes.tab.keystream
│   │   │   │   │   │   │   │       ├── class-attributes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── class-attributes.tab.len
│   │   │   │   │   │   │   │       ├── class-attributes.tab.values.at
│   │   │   │   │   │   │   │       ├── class-attributes.tab_i
│   │   │   │   │   │   │   │       ├── class-attributes.tab_i.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.values.at
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i.len
│   │   │   │   │   │   │   │       ├── constants.tab
│   │   │   │   │   │   │   │       ├── constants.tab.keystream
│   │   │   │   │   │   │   │       ├── constants.tab.keystream.len
│   │   │   │   │   │   │   │       ├── constants.tab.len
│   │   │   │   │   │   │   │       ├── constants.tab.values.at
│   │   │   │   │   │   │   │       ├── constants.tab_i
│   │   │   │   │   │   │   │       ├── constants.tab_i.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │   │   │   │   │       ├── proto.tab
│   │   │   │   │   │   │   │       ├── proto.tab.keystream
│   │   │   │   │   │   │   │       ├── proto.tab.keystream.len
│   │   │   │   │   │   │   │       ├── proto.tab.len
│   │   │   │   │   │   │   │       ├── proto.tab.values.at
│   │   │   │   │   │   │   │       ├── proto.tab_i
│   │   │   │   │   │   │   │       ├── proto.tab_i.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │   │   │   │   │       ├── source-to-classes.tab_i
│   │   │   │   │   │   │   │       ├── source-to-classes.tab_i.len
│   │   │   │   │   │   │   │       ├── subtypes.tab
│   │   │   │   │   │   │   │       ├── subtypes.tab.keystream
│   │   │   │   │   │   │   │       ├── subtypes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── subtypes.tab.len
│   │   │   │   │   │   │   │       ├── subtypes.tab.values.at
│   │   │   │   │   │   │   │       ├── subtypes.tab_i
│   │   │   │   │   │   │   │       ├── subtypes.tab_i.len
│   │   │   │   │   │   │   │       ├── supertypes.tab
│   │   │   │   │   │   │   │       ├── supertypes.tab.keystream
│   │   │   │   │   │   │   │       ├── supertypes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── supertypes.tab.len
│   │   │   │   │   │   │   │       ├── supertypes.tab.values.at
│   │   │   │   │   │   │   │       ├── supertypes.tab_i
│   │   │   │   │   │   │   │       └── supertypes.tab_i.len
│   │   │   │   │   │   │   └── lookups/
│   │   │   │   │   │   │       ├── counters.tab
│   │   │   │   │   │   │       ├── file-to-id.tab
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream.len
│   │   │   │   │   │   │       ├── file-to-id.tab.len
│   │   │   │   │   │   │       ├── file-to-id.tab.values.at
│   │   │   │   │   │   │       ├── file-to-id.tab_i
│   │   │   │   │   │   │       ├── file-to-id.tab_i.len
│   │   │   │   │   │   │       ├── id-to-file.tab
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream.len
│   │   │   │   │   │   │       ├── id-to-file.tab.len
│   │   │   │   │   │   │       ├── id-to-file.tab.values.at
│   │   │   │   │   │   │       ├── id-to-file.tab_i
│   │   │   │   │   │   │       ├── id-to-file.tab_i.len
│   │   │   │   │   │   │       ├── lookups.tab
│   │   │   │   │   │   │       ├── lookups.tab.keystream
│   │   │   │   │   │   │       ├── lookups.tab.keystream.len
│   │   │   │   │   │   │       ├── lookups.tab.len
│   │   │   │   │   │   │       ├── lookups.tab.values.at
│   │   │   │   │   │   │       ├── lookups.tab_i
│   │   │   │   │   │   │       └── lookups.tab_i.len
│   │   │   │   │   │   └── last-build.bin
│   │   │   │   │   ├── classpath-snapshot/
│   │   │   │   │   │   └── shrunk-classpath-snapshot.bin
│   │   │   │   │   └── local-state/
│   │   │   │   ├── compileDebugUnitTestKotlin/
│   │   │   │   │   ├── cacheable/
│   │   │   │   │   │   ├── caches-jvm/
│   │   │   │   │   │   │   ├── compilerPluginFiles/
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │   │   │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │   │   │   │   ├── inputs/
│   │   │   │   │   │   │   │   ├── source-to-output.tab
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.values.at
│   │   │   │   │   │   │   │   ├── source-to-output.tab_i
│   │   │   │   │   │   │   │   └── source-to-output.tab_i.len
│   │   │   │   │   │   │   ├── jvm/
│   │   │   │   │   │   │   │   └── kotlin/
│   │   │   │   │   │   │   │       ├── class-attributes.tab
│   │   │   │   │   │   │   │       ├── class-attributes.tab.keystream
│   │   │   │   │   │   │   │       ├── class-attributes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── class-attributes.tab.len
│   │   │   │   │   │   │   │       ├── class-attributes.tab.values.at
│   │   │   │   │   │   │   │       ├── class-attributes.tab_i
│   │   │   │   │   │   │   │       ├── class-attributes.tab_i.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.values.at
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │   │   │   │   │       ├── proto.tab
│   │   │   │   │   │   │   │       ├── proto.tab.keystream
│   │   │   │   │   │   │   │       ├── proto.tab.keystream.len
│   │   │   │   │   │   │   │       ├── proto.tab.len
│   │   │   │   │   │   │   │       ├── proto.tab.values.at
│   │   │   │   │   │   │   │       ├── proto.tab_i
│   │   │   │   │   │   │   │       ├── proto.tab_i.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │   │   │   │   │       ├── source-to-classes.tab_i
│   │   │   │   │   │   │   │       ├── source-to-classes.tab_i.len
│   │   │   │   │   │   │   │       ├── subtypes.tab
│   │   │   │   │   │   │   │       ├── subtypes.tab.keystream
│   │   │   │   │   │   │   │       ├── subtypes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── subtypes.tab.len
│   │   │   │   │   │   │   │       ├── subtypes.tab.values.at
│   │   │   │   │   │   │   │       ├── subtypes.tab_i
│   │   │   │   │   │   │   │       ├── subtypes.tab_i.len
│   │   │   │   │   │   │   │       ├── supertypes.tab
│   │   │   │   │   │   │   │       ├── supertypes.tab.keystream
│   │   │   │   │   │   │   │       ├── supertypes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── supertypes.tab.len
│   │   │   │   │   │   │   │       ├── supertypes.tab.values.at
│   │   │   │   │   │   │   │       ├── supertypes.tab_i
│   │   │   │   │   │   │   │       └── supertypes.tab_i.len
│   │   │   │   │   │   │   └── lookups/
│   │   │   │   │   │   │       ├── counters.tab
│   │   │   │   │   │   │       ├── file-to-id.tab
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream.len
│   │   │   │   │   │   │       ├── file-to-id.tab.len
│   │   │   │   │   │   │       ├── file-to-id.tab.values.at
│   │   │   │   │   │   │       ├── file-to-id.tab_i
│   │   │   │   │   │   │       ├── file-to-id.tab_i.len
│   │   │   │   │   │   │       ├── id-to-file.tab
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream.len
│   │   │   │   │   │   │       ├── id-to-file.tab.len
│   │   │   │   │   │   │       ├── id-to-file.tab.values.at
│   │   │   │   │   │   │       ├── id-to-file.tab_i
│   │   │   │   │   │   │       ├── id-to-file.tab_i.len
│   │   │   │   │   │   │       ├── lookups.tab
│   │   │   │   │   │   │       ├── lookups.tab.keystream
│   │   │   │   │   │   │       ├── lookups.tab.keystream.len
│   │   │   │   │   │   │       ├── lookups.tab.len
│   │   │   │   │   │   │       ├── lookups.tab.values.at
│   │   │   │   │   │   │       ├── lookups.tab_i
│   │   │   │   │   │   │       └── lookups.tab_i.len
│   │   │   │   │   │   └── last-build.bin
│   │   │   │   │   ├── classpath-snapshot/
│   │   │   │   │   │   └── shrunk-classpath-snapshot.bin
│   │   │   │   │   └── local-state/
│   │   │   │   └── compileReleaseKotlin/
│   │   │   │       ├── cacheable/
│   │   │   │       │   ├── caches-jvm/
│   │   │   │       │   │   ├── compilerPluginFiles/
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │       │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │       │   │   ├── inputs/
│   │   │   │       │   │   │   ├── source-to-output.tab
│   │   │   │       │   │   │   ├── source-to-output.tab.keystream
│   │   │   │       │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │       │   │   │   ├── source-to-output.tab.len
│   │   │   │       │   │   │   ├── source-to-output.tab.values.at
│   │   │   │       │   │   │   ├── source-to-output.tab_i
│   │   │   │       │   │   │   └── source-to-output.tab_i.len
│   │   │   │       │   │   ├── jvm/
│   │   │   │       │   │   │   └── kotlin/
│   │   │   │       │   │   │       ├── class-attributes.tab
│   │   │   │       │   │   │       ├── class-attributes.tab.keystream
│   │   │   │       │   │   │       ├── class-attributes.tab.keystream.len
│   │   │   │       │   │   │       ├── class-attributes.tab.len
│   │   │   │       │   │   │       ├── class-attributes.tab.values.at
│   │   │   │       │   │   │       ├── class-attributes.tab_i
│   │   │   │       │   │   │       ├── class-attributes.tab_i.len
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.len
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.values.at
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab_i
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab_i.len
│   │   │   │       │   │   │       ├── constants.tab
│   │   │   │       │   │   │       ├── constants.tab.keystream
│   │   │   │       │   │   │       ├── constants.tab.keystream.len
│   │   │   │       │   │   │       ├── constants.tab.len
│   │   │   │       │   │   │       ├── constants.tab.values.at
│   │   │   │       │   │   │       ├── constants.tab_i
│   │   │   │       │   │   │       ├── constants.tab_i.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │       │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │       │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │       │   │   │       ├── proto.tab
│   │   │   │       │   │   │       ├── proto.tab.keystream
│   │   │   │       │   │   │       ├── proto.tab.keystream.len
│   │   │   │       │   │   │       ├── proto.tab.len
│   │   │   │       │   │   │       ├── proto.tab.values.at
│   │   │   │       │   │   │       ├── proto.tab_i
│   │   │   │       │   │   │       ├── proto.tab_i.len
│   │   │   │       │   │   │       ├── source-to-classes.tab
│   │   │   │       │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │       │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │       │   │   │       ├── source-to-classes.tab.len
│   │   │   │       │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │       │   │   │       ├── source-to-classes.tab_i
│   │   │   │       │   │   │       ├── source-to-classes.tab_i.len
│   │   │   │       │   │   │       ├── subtypes.tab
│   │   │   │       │   │   │       ├── subtypes.tab.keystream
│   │   │   │       │   │   │       ├── subtypes.tab.keystream.len
│   │   │   │       │   │   │       ├── subtypes.tab.len
│   │   │   │       │   │   │       ├── subtypes.tab.values.at
│   │   │   │       │   │   │       ├── subtypes.tab_i
│   │   │   │       │   │   │       ├── subtypes.tab_i.len
│   │   │   │       │   │   │       ├── supertypes.tab
│   │   │   │       │   │   │       ├── supertypes.tab.keystream
│   │   │   │       │   │   │       ├── supertypes.tab.keystream.len
│   │   │   │       │   │   │       ├── supertypes.tab.len
│   │   │   │       │   │   │       ├── supertypes.tab.values.at
│   │   │   │       │   │   │       ├── supertypes.tab_i
│   │   │   │       │   │   │       └── supertypes.tab_i.len
│   │   │   │       │   │   └── lookups/
│   │   │   │       │   │       ├── counters.tab
│   │   │   │       │   │       ├── file-to-id.tab
│   │   │   │       │   │       ├── file-to-id.tab.keystream
│   │   │   │       │   │       ├── file-to-id.tab.keystream.len
│   │   │   │       │   │       ├── file-to-id.tab.len
│   │   │   │       │   │       ├── file-to-id.tab.values.at
│   │   │   │       │   │       ├── file-to-id.tab_i
│   │   │   │       │   │       ├── file-to-id.tab_i.len
│   │   │   │       │   │       ├── id-to-file.tab
│   │   │   │       │   │       ├── id-to-file.tab.keystream
│   │   │   │       │   │       ├── id-to-file.tab.keystream.len
│   │   │   │       │   │       ├── id-to-file.tab.len
│   │   │   │       │   │       ├── id-to-file.tab.values.at
│   │   │   │       │   │       ├── id-to-file.tab_i
│   │   │   │       │   │       ├── id-to-file.tab_i.len
│   │   │   │       │   │       ├── lookups.tab
│   │   │   │       │   │       ├── lookups.tab.keystream
│   │   │   │       │   │       ├── lookups.tab.keystream.len
│   │   │   │       │   │       ├── lookups.tab.len
│   │   │   │       │   │       ├── lookups.tab.values.at
│   │   │   │       │   │       ├── lookups.tab_i
│   │   │   │       │   │       └── lookups.tab_i.len
│   │   │   │       │   └── last-build.bin
│   │   │   │       ├── classpath-snapshot/
│   │   │   │       │   └── shrunk-classpath-snapshot.bin
│   │   │   │       └── local-state/
│   │   │   ├── kspCaches/
│   │   │   │   ├── debug/
│   │   │   │   │   ├── backups/java/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   │   │   ├── internal/
│   │   │   │   │   │   │   │   ├── EncryptedTokenStorage_Factory.java
│   │   │   │   │   │   │   │   └── FakeVaultSessionManager_Factory.java
│   │   │   │   │   │   │   ├── AuthErrorMapper_Factory.java
│   │   │   │   │   │   │   ├── AuthRepositoryImpl_Factory.java
│   │   │   │   │   │   │   ├── RemoteAuthDataSource_Factory.java
│   │   │   │   │   │   │   └── SessionManager_Factory.java
│   │   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_core_auth_di_AuthModule.java
│   │   │   │   │   ├── logs/
│   │   │   │   │   ├── symbolLookups/
│   │   │   │   │   │   ├── counters.tab
│   │   │   │   │   │   ├── file-to-id.tab
│   │   │   │   │   │   ├── file-to-id.tab.keystream
│   │   │   │   │   │   ├── file-to-id.tab.keystream.len
│   │   │   │   │   │   ├── file-to-id.tab.len
│   │   │   │   │   │   ├── file-to-id.tab.values.at
│   │   │   │   │   │   ├── file-to-id.tab_i
│   │   │   │   │   │   ├── file-to-id.tab_i.len
│   │   │   │   │   │   ├── id-to-file.tab
│   │   │   │   │   │   ├── id-to-file.tab.keystream
│   │   │   │   │   │   ├── id-to-file.tab.keystream.len
│   │   │   │   │   │   ├── id-to-file.tab.len
│   │   │   │   │   │   ├── id-to-file.tab.values.at
│   │   │   │   │   │   ├── id-to-file.tab_i
│   │   │   │   │   │   ├── id-to-file.tab_i.len
│   │   │   │   │   │   ├── lookups.tab
│   │   │   │   │   │   ├── lookups.tab.keystream
│   │   │   │   │   │   ├── lookups.tab.keystream.len
│   │   │   │   │   │   ├── lookups.tab.len
│   │   │   │   │   │   ├── lookups.tab.values.at
│   │   │   │   │   │   ├── lookups.tab_i
│   │   │   │   │   │   └── lookups.tab_i.len
│   │   │   │   │   ├── ap-classpath-entries.bin
│   │   │   │   │   ├── caches.uptodate
│   │   │   │   │   ├── classpath-entries.bin
│   │   │   │   │   ├── classpath-structure.bin
│   │   │   │   │   ├── sealed
│   │   │   │   │   ├── sourceToOutputs
│   │   │   │   │   └── symbols
│   │   │   │   ├── debugUnitTest/
│   │   │   │   │   ├── logs/
│   │   │   │   │   ├── symbolLookups/
│   │   │   │   │   │   ├── counters.tab
│   │   │   │   │   │   ├── file-to-id.tab
│   │   │   │   │   │   ├── file-to-id.tab.keystream
│   │   │   │   │   │   ├── file-to-id.tab.keystream.len
│   │   │   │   │   │   ├── file-to-id.tab.len
│   │   │   │   │   │   ├── file-to-id.tab.values.at
│   │   │   │   │   │   ├── file-to-id.tab_i
│   │   │   │   │   │   ├── file-to-id.tab_i.len
│   │   │   │   │   │   ├── id-to-file.tab
│   │   │   │   │   │   ├── id-to-file.tab.keystream
│   │   │   │   │   │   ├── id-to-file.tab.keystream.len
│   │   │   │   │   │   ├── id-to-file.tab.len
│   │   │   │   │   │   ├── id-to-file.tab.values.at
│   │   │   │   │   │   ├── id-to-file.tab_i
│   │   │   │   │   │   ├── id-to-file.tab_i.len
│   │   │   │   │   │   ├── lookups.tab
│   │   │   │   │   │   ├── lookups.tab.keystream
│   │   │   │   │   │   ├── lookups.tab.keystream.len
│   │   │   │   │   │   ├── lookups.tab.len
│   │   │   │   │   │   ├── lookups.tab.values.at
│   │   │   │   │   │   ├── lookups.tab_i
│   │   │   │   │   │   └── lookups.tab_i.len
│   │   │   │   │   ├── ap-classpath-entries.bin
│   │   │   │   │   ├── caches.uptodate
│   │   │   │   │   ├── classpath-entries.bin
│   │   │   │   │   ├── classpath-structure.bin
│   │   │   │   │   ├── sealed
│   │   │   │   │   ├── sourceToOutputs
│   │   │   │   │   └── symbols
│   │   │   │   └── release/
│   │   │   │       ├── backups/java/
│   │   │   │       │   ├── com/miguelrodriguez19/safecube/core/auth/
│   │   │   │       │   │   ├── internal/
│   │   │   │       │   │   │   ├── EncryptedTokenStorage_Factory.java
│   │   │   │       │   │   │   └── FakeVaultSessionManager_Factory.java
│   │   │   │       │   │   └── SessionManager_Factory.java
│   │   │   │       │   └── hilt_aggregated_deps/
│   │   │   │       │       └── _com_miguelrodriguez19_safecube_core_auth_di_AuthModule.java
│   │   │   │       ├── logs/
│   │   │   │       ├── symbolLookups/
│   │   │   │       │   ├── counters.tab
│   │   │   │       │   ├── file-to-id.tab
│   │   │   │       │   ├── file-to-id.tab.keystream
│   │   │   │       │   ├── file-to-id.tab.keystream.len
│   │   │   │       │   ├── file-to-id.tab.len
│   │   │   │       │   ├── file-to-id.tab.values.at
│   │   │   │       │   ├── file-to-id.tab_i
│   │   │   │       │   ├── file-to-id.tab_i.len
│   │   │   │       │   ├── id-to-file.tab
│   │   │   │       │   ├── id-to-file.tab.keystream
│   │   │   │       │   ├── id-to-file.tab.keystream.len
│   │   │   │       │   ├── id-to-file.tab.len
│   │   │   │       │   ├── id-to-file.tab.values.at
│   │   │   │       │   ├── id-to-file.tab_i
│   │   │   │       │   ├── id-to-file.tab_i.len
│   │   │   │       │   ├── lookups.tab
│   │   │   │       │   ├── lookups.tab.keystream
│   │   │   │       │   ├── lookups.tab.keystream.len
│   │   │   │       │   ├── lookups.tab.len
│   │   │   │       │   ├── lookups.tab.values.at
│   │   │   │       │   ├── lookups.tab_i
│   │   │   │       │   └── lookups.tab_i.len
│   │   │   │       ├── ap-classpath-entries.bin
│   │   │   │       ├── caches.uptodate
│   │   │   │       ├── classpath-entries.bin
│   │   │   │       ├── classpath-structure.bin
│   │   │   │       ├── sealed
│   │   │   │       ├── sourceToOutputs
│   │   │   │       └── symbols
│   │   │   ├── outputs/
│   │   │   │   ├── aar/
│   │   │   │   │   └── auth-debug.aar
│   │   │   │   ├── androidTest-results/connected/debug/
│   │   │   │   ├── apk/androidTest/debug/
│   │   │   │   │   ├── auth-debug-androidTest.apk
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── code_coverage/debugAndroidTest/connected/
│   │   │   │   │   └── coverage.ec
│   │   │   │   ├── connected_android_test_additional_output/debugAndroidTest/connected/
│   │   │   │   └── logs/
│   │   │   │       ├── manifest-merger-debug-report.txt
│   │   │   │       └── manifest-merger-release-report.txt
│   │   │   ├── reports/
│   │   │   │   ├── androidTests/connected/debug/
│   │   │   │   │   ├── css/
│   │   │   │   │   │   ├── base-style.css
│   │   │   │   │   │   └── style.css
│   │   │   │   │   ├── js/
│   │   │   │   │   │   └── report.js
│   │   │   │   │   └── index.html
│   │   │   │   └── tests/testDebugUnitTest/
│   │   │   │       ├── classes/
│   │   │   │       │   ├── com.miguelrodriguez19.safecube.core.auth.AuthErrorMapperTest.html
│   │   │   │       │   ├── com.miguelrodriguez19.safecube.core.auth.AuthRepositoryImplTest.html
│   │   │   │       │   └── com.miguelrodriguez19.safecube.core.auth.RemoteAuthDataSourceTest.html
│   │   │   │       ├── css/
│   │   │   │       │   ├── base-style.css
│   │   │   │       │   └── style.css
│   │   │   │       ├── js/
│   │   │   │       │   └── report.js
│   │   │   │       ├── packages/
│   │   │   │       │   └── com.miguelrodriguez19.safecube.core.auth.html
│   │   │   │       └── index.html
│   │   │   ├── test-results/testDebugUnitTest/
│   │   │   │   ├── binary/
│   │   │   │   │   ├── output.bin
│   │   │   │   │   ├── output.bin.idx
│   │   │   │   │   └── results.bin
│   │   │   │   ├── TEST-com.miguelrodriguez19.safecube.core.auth.AuthErrorMapperTest.xml
│   │   │   │   ├── TEST-com.miguelrodriguez19.safecube.core.auth.AuthRepositoryImplTest.xml
│   │   │   │   └── TEST-com.miguelrodriguez19.safecube.core.auth.RemoteAuthDataSourceTest.xml
│   │   │   └── tmp/
│   │   │       ├── compileDebugJavaWithJavac/
│   │   │       │   ├── compileTransaction/
│   │   │       │   │   ├── backup-dir/
│   │   │       │   │   └── stash-dir/
│   │   │       │   └── previous-compilation-data.bin
│   │   │       ├── compileReleaseJavaWithJavac/
│   │   │       │   ├── compileTransaction/
│   │   │       │   │   ├── backup-dir/
│   │   │       │   │   └── stash-dir/
│   │   │       │   │       ├── EncryptedTokenStorage_Factory.class.uniqueId1
│   │   │       │   │       └── SessionManager_Factory.class.uniqueId0
│   │   │       │   └── previous-compilation-data.bin
│   │   │       └── testDebugUnitTest/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   ├── di/
│   │   │   │   │   │   └── AuthModule.kt
│   │   │   │   │   ├── internal/
│   │   │   │   │   │   ├── EncryptedTokenStorage.kt
│   │   │   │   │   │   └── FakeVaultSessionManager.kt
│   │   │   │   │   ├── AuthError.kt
│   │   │   │   │   ├── AuthErrorMapper.kt
│   │   │   │   │   ├── AuthOperation.kt
│   │   │   │   │   ├── AuthRepository.kt
│   │   │   │   │   ├── AuthRepositoryImpl.kt
│   │   │   │   │   ├── AuthResult.kt
│   │   │   │   │   ├── AuthTokens.kt
│   │   │   │   │   ├── NetworkResult.kt
│   │   │   │   │   ├── RegisteredAccount.kt
│   │   │   │   │   ├── RemoteAuthDataSource.kt
│   │   │   │   │   ├── SessionManager.kt
│   │   │   │   │   ├── SessionState.kt
│   │   │   │   │   ├── TokenStorage.kt
│   │   │   │   │   ├── VaultSessionManager.kt
│   │   │   │   │   └── VaultState.kt
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/java/com/miguelrodriguez19/safecube/core/auth/
│   │   │       ├── AuthErrorMapperTest.kt
│   │   │       ├── AuthRepositoryImplTest.kt
│   │   │       └── RemoteAuthDataSourceTest.kt
│   │   └── build.gradle.kts
│   ├── crypto/
│   │   ├── build/
│   │   │   ├── .transforms/
│   │   │   │   ├── 13b3fbe47799c89b06019967d5966e55/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │   │   │   ├── CryptoEngine.dex
│   │   │   │   │   │   │   ├── DecryptionRequest.dex
│   │   │   │   │   │   │   ├── EncryptionRequest.dex
│   │   │   │   │   │   │   ├── EncryptionResult.dex
│   │   │   │   │   │   │   ├── KdfEngine.dex
│   │   │   │   │   │   │   ├── KdfRequest.dex
│   │   │   │   │   │   │   ├── KeyUnwrapRequest.dex
│   │   │   │   │   │   │   ├── KeyWrapping.dex
│   │   │   │   │   │   │   └── KeyWrapRequest.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 2ca3dfe76acec6f9eed806e96d094c8f/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 2ce6281333f098be07455dda531fc9bb/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │   │   │   ├── CryptoEngine.dex
│   │   │   │   │   │   │   ├── DecryptionRequest.dex
│   │   │   │   │   │   │   ├── EncryptionRequest.dex
│   │   │   │   │   │   │   ├── EncryptionResult.dex
│   │   │   │   │   │   │   ├── KdfEngine.dex
│   │   │   │   │   │   │   ├── KdfRequest.dex
│   │   │   │   │   │   │   ├── KeyUnwrapRequest.dex
│   │   │   │   │   │   │   ├── KeyWrapping.dex
│   │   │   │   │   │   │   └── KeyWrapRequest.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 3c0a33c08f0a9b08ba789ddf200927e4/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │   │   │   ├── CryptoEngine.dex
│   │   │   │   │   │   │   ├── DecryptionRequest.dex
│   │   │   │   │   │   │   ├── EncryptionRequest.dex
│   │   │   │   │   │   │   ├── EncryptionResult.dex
│   │   │   │   │   │   │   ├── KdfEngine.dex
│   │   │   │   │   │   │   ├── KdfRequest.dex
│   │   │   │   │   │   │   ├── KeyUnwrapRequest.dex
│   │   │   │   │   │   │   ├── KeyWrapping.dex
│   │   │   │   │   │   │   └── KeyWrapRequest.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 52c65a7dc4a01cc5e52550fca89f11a7/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   └── CryptoModule.dex
│   │   │   │   │   │   │   ├── CryptoEngine.dex
│   │   │   │   │   │   │   ├── DecryptionRequest.dex
│   │   │   │   │   │   │   ├── EncryptionRequest.dex
│   │   │   │   │   │   │   ├── EncryptionResult.dex
│   │   │   │   │   │   │   ├── KdfEngine.dex
│   │   │   │   │   │   │   ├── KdfRequest.dex
│   │   │   │   │   │   │   ├── KeyUnwrapRequest.dex
│   │   │   │   │   │   │   ├── KeyWrapping.dex
│   │   │   │   │   │   │   └── KeyWrapRequest.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_crypto_di_CryptoModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 674f07555c3e2319a1795bfbd0b92b08/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   └── CryptoModule.dex
│   │   │   │   │   │   │   ├── CryptoEngine.dex
│   │   │   │   │   │   │   ├── DecryptionRequest.dex
│   │   │   │   │   │   │   ├── EncryptionRequest.dex
│   │   │   │   │   │   │   ├── EncryptionResult.dex
│   │   │   │   │   │   │   ├── KdfEngine.dex
│   │   │   │   │   │   │   ├── KdfRequest.dex
│   │   │   │   │   │   │   ├── KeyUnwrapRequest.dex
│   │   │   │   │   │   │   ├── KeyWrapping.dex
│   │   │   │   │   │   │   └── KeyWrapRequest.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_crypto_di_CryptoModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 7cbbe691d3a53e32b9796baa59d765ff/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │   │   │   ├── CryptoEngine.dex
│   │   │   │   │   │   │   ├── DecryptionRequest.dex
│   │   │   │   │   │   │   ├── EncryptionRequest.dex
│   │   │   │   │   │   │   ├── EncryptionResult.dex
│   │   │   │   │   │   │   ├── KdfEngine.dex
│   │   │   │   │   │   │   ├── KdfRequest.dex
│   │   │   │   │   │   │   ├── KeyUnwrapRequest.dex
│   │   │   │   │   │   │   ├── KeyWrapping.dex
│   │   │   │   │   │   │   └── KeyWrapRequest.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 8a29a43728cc22178dbcaebdc15dfb6d/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   └── CryptoModule.dex
│   │   │   │   │   │   │   ├── internal/
│   │   │   │   │   │   │   │   ├── FakeCryptoEngine.dex
│   │   │   │   │   │   │   │   ├── FakeCryptoEngine_Factory$InstanceHolder.dex
│   │   │   │   │   │   │   │   └── FakeCryptoEngine_Factory.dex
│   │   │   │   │   │   │   ├── CryptoEngine.dex
│   │   │   │   │   │   │   ├── DecryptionRequest.dex
│   │   │   │   │   │   │   ├── EncryptionRequest.dex
│   │   │   │   │   │   │   ├── EncryptionResult.dex
│   │   │   │   │   │   │   ├── KdfEngine.dex
│   │   │   │   │   │   │   ├── KdfRequest.dex
│   │   │   │   │   │   │   ├── KeyUnwrapRequest.dex
│   │   │   │   │   │   │   ├── KeyWrapping.dex
│   │   │   │   │   │   │   └── KeyWrapRequest.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_crypto_di_CryptoModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 932bfcb4b8ffd28698be5b06fc04f382/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── a298670def383a296453c63d7bb5d1af/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── d77fe5e285631bdc1235d63e8b93aa5e/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   └── e899130ce2ce74908f0a39b6961ebd2c/
│   │   │   │       ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │       │   ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │       │   │   ├── di/
│   │   │   │       │   │   │   └── CryptoModule.dex
│   │   │   │       │   │   ├── internal/
│   │   │   │       │   │   │   ├── FakeCryptoEngine.dex
│   │   │   │       │   │   │   ├── FakeCryptoEngine_Factory$InstanceHolder.dex
│   │   │   │       │   │   │   └── FakeCryptoEngine_Factory.dex
│   │   │   │       │   │   ├── CryptoEngine.dex
│   │   │   │       │   │   ├── DecryptionRequest.dex
│   │   │   │       │   │   ├── EncryptionRequest.dex
│   │   │   │       │   │   ├── EncryptionResult.dex
│   │   │   │       │   │   ├── KdfEngine.dex
│   │   │   │       │   │   ├── KdfRequest.dex
│   │   │   │       │   │   ├── KeyUnwrapRequest.dex
│   │   │   │       │   │   ├── KeyWrapping.dex
│   │   │   │       │   │   └── KeyWrapRequest.dex
│   │   │   │       │   ├── hilt_aggregated_deps/
│   │   │   │       │   │   └── _com_miguelrodriguez19_safecube_core_crypto_di_CryptoModule.dex
│   │   │   │       │   └── desugar_graph.bin
│   │   │   │       └── results.bin
│   │   │   ├── generated/
│   │   │   │   ├── ap_generated_sources/
│   │   │   │   │   ├── debug/out/
│   │   │   │   │   └── release/out/
│   │   │   │   ├── ksp/
│   │   │   │   │   ├── debug/java/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/internal/
│   │   │   │   │   │   │   └── FakeCryptoEngine_Factory.java
│   │   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_core_crypto_di_CryptoModule.java
│   │   │   │   │   └── release/java/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/crypto/internal/
│   │   │   │   │       │   └── FakeCryptoEngine_Factory.java
│   │   │   │   │       └── hilt_aggregated_deps/
│   │   │   │   │           └── _com_miguelrodriguez19_safecube_core_crypto_di_CryptoModule.java
│   │   │   │   ├── res/pngs/
│   │   │   │   │   ├── debug/
│   │   │   │   │   └── release/
│   │   │   │   └── updated_navigation_xml/
│   │   │   │       ├── debug/
│   │   │   │       ├── debugAndroidTest/
│   │   │   │       └── release/
│   │   │   ├── intermediates/
│   │   │   │   ├── aapt_friendly_merged_manifests/
│   │   │   │   │   ├── debug/processDebugManifest/aapt/
│   │   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   │   └── output-metadata.json
│   │   │   │   │   └── release/processReleaseManifest/aapt/
│   │   │   │   │       ├── AndroidManifest.xml
│   │   │   │   │       └── output-metadata.json
│   │   │   │   ├── aar_libs_directory/debug/syncDebugLibJars/libs/
│   │   │   │   ├── aar_main_jar/debug/syncDebugLibJars/
│   │   │   │   │   └── classes.jar
│   │   │   │   ├── aar_metadata/
│   │   │   │   │   ├── debug/writeDebugAarMetadata/
│   │   │   │   │   │   │   │   │   │   │   └── release/writeReleaseAarMetadata/
│   │   │   │   │       │   │   │   │   ├── aar_metadata_check/
│   │   │   │   │   ├── debug/checkDebugAarMetadata/
│   │   │   │   │   └── debugAndroidTest/checkDebugAndroidTestAarMetadata/
│   │   │   │   ├── android_res_source_set_path_map/debugAndroidTest/mapDebugAndroidTestSourceSetPaths/
│   │   │   │   │   └── file-map.txt
│   │   │   │   ├── annotation_processor_list/
│   │   │   │   │   ├── debug/javaPreCompileDebug/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   ├── debugAndroidTest/javaPreCompileDebugAndroidTest/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   ├── debugUnitTest/javaPreCompileDebugUnitTest/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   └── release/javaPreCompileRelease/
│   │   │   │   │       └── annotationProcessors.json
│   │   │   │   ├── annotations_typedef_file/debug/extractDebugAnnotations/
│   │   │   │   │   └── typedefs.txt
│   │   │   │   ├── annotations_zip/debug/extractDebugAnnotations/
│   │   │   │   ├── apk_ide_redirect_file/debugAndroidTest/createDebugAndroidTestApkListingFileRedirect/
│   │   │   │   │   └── redirect.txt
│   │   │   │   ├── assets/
│   │   │   │   │   ├── debug/mergeDebugAssets/
│   │   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestAssets/
│   │   │   │   │   └── release/mergeReleaseAssets/
│   │   │   │   ├── built_in_kotlinc/
│   │   │   │   │   ├── debug/compileDebugKotlin/classes/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   └── CryptoModule.class
│   │   │   │   │   │   │   ├── internal/
│   │   │   │   │   │   │   │   └── FakeCryptoEngine.class
│   │   │   │   │   │   │   ├── CryptoEngine.class
│   │   │   │   │   │   │   ├── DecryptionRequest.class
│   │   │   │   │   │   │   ├── EncryptionRequest.class
│   │   │   │   │   │   │   ├── EncryptionResult.class
│   │   │   │   │   │   │   ├── KdfEngine.class
│   │   │   │   │   │   │   ├── KdfRequest.class
│   │   │   │   │   │   │   ├── KeyUnwrapRequest.class
│   │   │   │   │   │   │   ├── KeyWrapping.class
│   │   │   │   │   │   │   └── KeyWrapRequest.class
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── crypto.kotlin_module
│   │   │   │   │   └── release/compileReleaseKotlin/classes/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │       │   ├── di/
│   │   │   │   │       │   │   └── CryptoModule.class
│   │   │   │   │       │   ├── internal/
│   │   │   │   │       │   │   └── FakeCryptoEngine.class
│   │   │   │   │       │   ├── CryptoEngine.class
│   │   │   │   │       │   ├── DecryptionRequest.class
│   │   │   │   │       │   ├── EncryptionRequest.class
│   │   │   │   │       │   ├── EncryptionResult.class
│   │   │   │   │       │   ├── KdfEngine.class
│   │   │   │   │       │   ├── KdfRequest.class
│   │   │   │   │       │   ├── KeyUnwrapRequest.class
│   │   │   │   │       │   ├── KeyWrapping.class
│   │   │   │   │       │   └── KeyWrapRequest.class
│   │   │   │   │       └── META-INF/
│   │   │   │   │           └── crypto.kotlin_module
│   │   │   │   ├── classes/
│   │   │   │   │   ├── debug/transformDebugClassesWithAsm/
│   │   │   │   │   │   ├── dirs/
│   │   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   │   └── CryptoModule.class
│   │   │   │   │   │   │   │   ├── internal/
│   │   │   │   │   │   │   │   │   ├── FakeCryptoEngine.class
│   │   │   │   │   │   │   │   │   ├── FakeCryptoEngine_Factory$InstanceHolder.class
│   │   │   │   │   │   │   │   │   └── FakeCryptoEngine_Factory.class
│   │   │   │   │   │   │   │   ├── CryptoEngine.class
│   │   │   │   │   │   │   │   ├── DecryptionRequest.class
│   │   │   │   │   │   │   │   ├── EncryptionRequest.class
│   │   │   │   │   │   │   │   ├── EncryptionResult.class
│   │   │   │   │   │   │   │   ├── KdfEngine.class
│   │   │   │   │   │   │   │   ├── KdfRequest.class
│   │   │   │   │   │   │   │   ├── KeyUnwrapRequest.class
│   │   │   │   │   │   │   │   ├── KeyWrapping.class
│   │   │   │   │   │   │   │   └── KeyWrapRequest.class
│   │   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_crypto_di_CryptoModule.class
│   │   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │   │       └── crypto.kotlin_module
│   │   │   │   │   │   └── jars/
│   │   │   │   │   └── release/transformReleaseClassesWithAsm/
│   │   │   │   │       ├── dirs/
│   │   │   │   │       │   ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │       │   │   ├── di/
│   │   │   │   │       │   │   │   └── CryptoModule.class
│   │   │   │   │       │   │   ├── internal/
│   │   │   │   │       │   │   │   ├── FakeCryptoEngine.class
│   │   │   │   │       │   │   │   ├── FakeCryptoEngine_Factory$InstanceHolder.class
│   │   │   │   │       │   │   │   └── FakeCryptoEngine_Factory.class
│   │   │   │   │       │   │   ├── CryptoEngine.class
│   │   │   │   │       │   │   ├── DecryptionRequest.class
│   │   │   │   │       │   │   ├── EncryptionRequest.class
│   │   │   │   │       │   │   ├── EncryptionResult.class
│   │   │   │   │       │   │   ├── KdfEngine.class
│   │   │   │   │       │   │   ├── KdfRequest.class
│   │   │   │   │       │   │   ├── KeyUnwrapRequest.class
│   │   │   │   │       │   │   ├── KeyWrapping.class
│   │   │   │   │       │   │   └── KeyWrapRequest.class
│   │   │   │   │       │   ├── hilt_aggregated_deps/
│   │   │   │   │       │   │   └── _com_miguelrodriguez19_safecube_core_crypto_di_CryptoModule.class
│   │   │   │   │       │   └── META-INF/
│   │   │   │   │       │       └── crypto.kotlin_module
│   │   │   │   │       └── jars/
│   │   │   │   ├── compile_and_runtime_r_class_jar/
│   │   │   │   │   ├── debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   └── debugUnitTest/generateDebugUnitTestStubRFile/
│   │   │   │   │       └── R.jar
│   │   │   │   ├── compile_library_classes_jar/
│   │   │   │   │   ├── debug/bundleLibCompileToJarDebug/
│   │   │   │   │   │   └── classes.jar
│   │   │   │   │   └── release/bundleLibCompileToJarRelease/
│   │   │   │   │       └── classes.jar
│   │   │   │   ├── compile_r_class_jar/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── R.jar
│   │   │   │   ├── compile_symbol_list/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── R.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── R.txt
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── R.txt
│   │   │   │   ├── compiled_local_resources/debug/compileDebugLibraryResources/out/
│   │   │   │   ├── compiled_navigation_res/debugAndroidTest/compileDebugAndroidTestNavigationResources/
│   │   │   │   ├── compressed_assets/debugAndroidTest/compressDebugAndroidTestAssets/out/
│   │   │   │   ├── consumer_proguard_dir/release/
│   │   │   │   ├── data_binding_layout_info_type_merge/debugAndroidTest/mergeDebugAndroidTestResources/out/
│   │   │   │   ├── data_binding_layout_info_type_package/
│   │   │   │   │   ├── debug/packageDebugResources/out/
│   │   │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/out/
│   │   │   │   │   └── release/packageReleaseResources/out/
│   │   │   │   ├── desugar_graph/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   │   ├── currentProject/
│   │   │   │   │   │   ├── jar_a70c0dac0436bcef7c2fa406d2866ea88e28a2ee066ab54adaac7314a8471b81_bucket_0/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_a70c0dac0436bcef7c2fa406d2866ea88e28a2ee066ab54adaac7314a8471b81_bucket_1/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_a70c0dac0436bcef7c2fa406d2866ea88e28a2ee066ab54adaac7314a8471b81_bucket_2/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_a70c0dac0436bcef7c2fa406d2866ea88e28a2ee066ab54adaac7314a8471b81_bucket_3/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_a70c0dac0436bcef7c2fa406d2866ea88e28a2ee066ab54adaac7314a8471b81_bucket_4/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   └── jar_a70c0dac0436bcef7c2fa406d2866ea88e28a2ee066ab54adaac7314a8471b81_bucket_5/
│   │   │   │   │   │       └── graph.bin
│   │   │   │   │   ├── externalLibs/
│   │   │   │   │   ├── mixedScopes/
│   │   │   │   │   └── otherProjects/
│   │   │   │   ├── dex/debugAndroidTest/
│   │   │   │   │   ├── mergeExtDexDebugAndroidTest/
│   │   │   │   │   │   └── classes.dex
│   │   │   │   │   ├── mergeLibDexDebugAndroidTest/
│   │   │   │   │   │   ├── 0/
│   │   │   │   │   │   ├── 1/
│   │   │   │   │   │   ├── 10/
│   │   │   │   │   │   ├── 11/
│   │   │   │   │   │   ├── 12/
│   │   │   │   │   │   ├── 13/
│   │   │   │   │   │   ├── 14/
│   │   │   │   │   │   ├── 15/
│   │   │   │   │   │   ├── 2/
│   │   │   │   │   │   ├── 3/
│   │   │   │   │   │   ├── 4/
│   │   │   │   │   │   ├── 5/
│   │   │   │   │   │   ├── 6/
│   │   │   │   │   │   ├── 7/
│   │   │   │   │   │   ├── 8/
│   │   │   │   │   │   └── 9/
│   │   │   │   │   └── mergeProjectDexDebugAndroidTest/
│   │   │   │   │       ├── 0/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 1/
│   │   │   │   │       ├── 10/
│   │   │   │   │       ├── 11/
│   │   │   │   │       ├── 12/
│   │   │   │   │       ├── 13/
│   │   │   │   │       ├── 14/
│   │   │   │   │       ├── 15/
│   │   │   │   │       ├── 2/
│   │   │   │   │       ├── 3/
│   │   │   │   │       ├── 4/
│   │   │   │   │       ├── 5/
│   │   │   │   │       ├── 6/
│   │   │   │   │       ├── 7/
│   │   │   │   │       ├── 8/
│   │   │   │   │       └── 9/
│   │   │   │   ├── dex_archive_input_jar_hashes/debugAndroidTest/dexBuilderDebugAndroidTest/
│   │   │   │   │   └── out
│   │   │   │   ├── dex_number_of_buckets_file/debugAndroidTest/dexBuilderDebugAndroidTest/
│   │   │   │   │   └── out
│   │   │   │   ├── duplicate_classes_check/debugAndroidTest/checkDebugAndroidTestDuplicateClasses/
│   │   │   │   ├── external_file_lib_dex_archives/debugAndroidTest/desugarDebugAndroidTestFileDependencies/
│   │   │   │   ├── external_libs_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── external_libs_dex_archive_with_artifact_transforms/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── generated_proguard_file/
│   │   │   │   │   ├── debug/mergeDebugGeneratedProguardFiles/
│   │   │   │   │   └── release/mergeReleaseGeneratedProguardFiles/
│   │   │   │   ├── incremental/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── debug-mergeJavaRes/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   └── merge-state
│   │   │   │   │   ├── debugAndroidTest/
│   │   │   │   │   │   ├── mergeDebugAndroidTestResources/
│   │   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   │   │   ├── values/
│   │   │   │   │   │   │   │   │   └── values.xml
│   │   │   │   │   │   │   │   ├── values-af/
│   │   │   │   │   │   │   │   │   └── values-af.xml
│   │   │   │   │   │   │   │   ├── values-am/
│   │   │   │   │   │   │   │   │   └── values-am.xml
│   │   │   │   │   │   │   │   ├── values-ar/
│   │   │   │   │   │   │   │   │   └── values-ar.xml
│   │   │   │   │   │   │   │   ├── values-as/
│   │   │   │   │   │   │   │   │   └── values-as.xml
│   │   │   │   │   │   │   │   ├── values-az/
│   │   │   │   │   │   │   │   │   └── values-az.xml
│   │   │   │   │   │   │   │   ├── values-b+sr+Latn/
│   │   │   │   │   │   │   │   │   └── values-b+sr+Latn.xml
│   │   │   │   │   │   │   │   ├── values-be/
│   │   │   │   │   │   │   │   │   └── values-be.xml
│   │   │   │   │   │   │   │   ├── values-bg/
│   │   │   │   │   │   │   │   │   └── values-bg.xml
│   │   │   │   │   │   │   │   ├── values-bn/
│   │   │   │   │   │   │   │   │   └── values-bn.xml
│   │   │   │   │   │   │   │   ├── values-bs/
│   │   │   │   │   │   │   │   │   └── values-bs.xml
│   │   │   │   │   │   │   │   ├── values-ca/
│   │   │   │   │   │   │   │   │   └── values-ca.xml
│   │   │   │   │   │   │   │   ├── values-cs/
│   │   │   │   │   │   │   │   │   └── values-cs.xml
│   │   │   │   │   │   │   │   ├── values-da/
│   │   │   │   │   │   │   │   │   └── values-da.xml
│   │   │   │   │   │   │   │   ├── values-de/
│   │   │   │   │   │   │   │   │   └── values-de.xml
│   │   │   │   │   │   │   │   ├── values-el/
│   │   │   │   │   │   │   │   │   └── values-el.xml
│   │   │   │   │   │   │   │   ├── values-en-rAU/
│   │   │   │   │   │   │   │   │   └── values-en-rAU.xml
│   │   │   │   │   │   │   │   ├── values-en-rCA/
│   │   │   │   │   │   │   │   │   └── values-en-rCA.xml
│   │   │   │   │   │   │   │   ├── values-en-rGB/
│   │   │   │   │   │   │   │   │   └── values-en-rGB.xml
│   │   │   │   │   │   │   │   ├── values-en-rIN/
│   │   │   │   │   │   │   │   │   └── values-en-rIN.xml
│   │   │   │   │   │   │   │   ├── values-en-rXC/
│   │   │   │   │   │   │   │   │   └── values-en-rXC.xml
│   │   │   │   │   │   │   │   ├── values-es/
│   │   │   │   │   │   │   │   │   └── values-es.xml
│   │   │   │   │   │   │   │   ├── values-es-rUS/
│   │   │   │   │   │   │   │   │   └── values-es-rUS.xml
│   │   │   │   │   │   │   │   ├── values-et/
│   │   │   │   │   │   │   │   │   └── values-et.xml
│   │   │   │   │   │   │   │   ├── values-eu/
│   │   │   │   │   │   │   │   │   └── values-eu.xml
│   │   │   │   │   │   │   │   ├── values-fa/
│   │   │   │   │   │   │   │   │   └── values-fa.xml
│   │   │   │   │   │   │   │   ├── values-fi/
│   │   │   │   │   │   │   │   │   └── values-fi.xml
│   │   │   │   │   │   │   │   ├── values-fr/
│   │   │   │   │   │   │   │   │   └── values-fr.xml
│   │   │   │   │   │   │   │   ├── values-fr-rCA/
│   │   │   │   │   │   │   │   │   └── values-fr-rCA.xml
│   │   │   │   │   │   │   │   ├── values-gl/
│   │   │   │   │   │   │   │   │   └── values-gl.xml
│   │   │   │   │   │   │   │   ├── values-gu/
│   │   │   │   │   │   │   │   │   └── values-gu.xml
│   │   │   │   │   │   │   │   ├── values-hi/
│   │   │   │   │   │   │   │   │   └── values-hi.xml
│   │   │   │   │   │   │   │   ├── values-hr/
│   │   │   │   │   │   │   │   │   └── values-hr.xml
│   │   │   │   │   │   │   │   ├── values-hu/
│   │   │   │   │   │   │   │   │   └── values-hu.xml
│   │   │   │   │   │   │   │   ├── values-hy/
│   │   │   │   │   │   │   │   │   └── values-hy.xml
│   │   │   │   │   │   │   │   ├── values-in/
│   │   │   │   │   │   │   │   │   └── values-in.xml
│   │   │   │   │   │   │   │   ├── values-is/
│   │   │   │   │   │   │   │   │   └── values-is.xml
│   │   │   │   │   │   │   │   ├── values-it/
│   │   │   │   │   │   │   │   │   └── values-it.xml
│   │   │   │   │   │   │   │   ├── values-iw/
│   │   │   │   │   │   │   │   │   └── values-iw.xml
│   │   │   │   │   │   │   │   ├── values-ja/
│   │   │   │   │   │   │   │   │   └── values-ja.xml
│   │   │   │   │   │   │   │   ├── values-ka/
│   │   │   │   │   │   │   │   │   └── values-ka.xml
│   │   │   │   │   │   │   │   ├── values-kk/
│   │   │   │   │   │   │   │   │   └── values-kk.xml
│   │   │   │   │   │   │   │   ├── values-km/
│   │   │   │   │   │   │   │   │   └── values-km.xml
│   │   │   │   │   │   │   │   ├── values-kn/
│   │   │   │   │   │   │   │   │   └── values-kn.xml
│   │   │   │   │   │   │   │   ├── values-ko/
│   │   │   │   │   │   │   │   │   └── values-ko.xml
│   │   │   │   │   │   │   │   ├── values-ky/
│   │   │   │   │   │   │   │   │   └── values-ky.xml
│   │   │   │   │   │   │   │   ├── values-lo/
│   │   │   │   │   │   │   │   │   └── values-lo.xml
│   │   │   │   │   │   │   │   ├── values-lt/
│   │   │   │   │   │   │   │   │   └── values-lt.xml
│   │   │   │   │   │   │   │   ├── values-lv/
│   │   │   │   │   │   │   │   │   └── values-lv.xml
│   │   │   │   │   │   │   │   ├── values-mk/
│   │   │   │   │   │   │   │   │   └── values-mk.xml
│   │   │   │   │   │   │   │   ├── values-ml/
│   │   │   │   │   │   │   │   │   └── values-ml.xml
│   │   │   │   │   │   │   │   ├── values-mn/
│   │   │   │   │   │   │   │   │   └── values-mn.xml
│   │   │   │   │   │   │   │   ├── values-mr/
│   │   │   │   │   │   │   │   │   └── values-mr.xml
│   │   │   │   │   │   │   │   ├── values-ms/
│   │   │   │   │   │   │   │   │   └── values-ms.xml
│   │   │   │   │   │   │   │   ├── values-my/
│   │   │   │   │   │   │   │   │   └── values-my.xml
│   │   │   │   │   │   │   │   ├── values-nb/
│   │   │   │   │   │   │   │   │   └── values-nb.xml
│   │   │   │   │   │   │   │   ├── values-ne/
│   │   │   │   │   │   │   │   │   └── values-ne.xml
│   │   │   │   │   │   │   │   ├── values-nl/
│   │   │   │   │   │   │   │   │   └── values-nl.xml
│   │   │   │   │   │   │   │   ├── values-or/
│   │   │   │   │   │   │   │   │   └── values-or.xml
│   │   │   │   │   │   │   │   ├── values-pa/
│   │   │   │   │   │   │   │   │   └── values-pa.xml
│   │   │   │   │   │   │   │   ├── values-pl/
│   │   │   │   │   │   │   │   │   └── values-pl.xml
│   │   │   │   │   │   │   │   ├── values-pt/
│   │   │   │   │   │   │   │   │   └── values-pt.xml
│   │   │   │   │   │   │   │   ├── values-pt-rBR/
│   │   │   │   │   │   │   │   │   └── values-pt-rBR.xml
│   │   │   │   │   │   │   │   ├── values-pt-rPT/
│   │   │   │   │   │   │   │   │   └── values-pt-rPT.xml
│   │   │   │   │   │   │   │   ├── values-ro/
│   │   │   │   │   │   │   │   │   └── values-ro.xml
│   │   │   │   │   │   │   │   ├── values-ru/
│   │   │   │   │   │   │   │   │   └── values-ru.xml
│   │   │   │   │   │   │   │   ├── values-si/
│   │   │   │   │   │   │   │   │   └── values-si.xml
│   │   │   │   │   │   │   │   ├── values-sk/
│   │   │   │   │   │   │   │   │   └── values-sk.xml
│   │   │   │   │   │   │   │   ├── values-sl/
│   │   │   │   │   │   │   │   │   └── values-sl.xml
│   │   │   │   │   │   │   │   ├── values-sq/
│   │   │   │   │   │   │   │   │   └── values-sq.xml
│   │   │   │   │   │   │   │   ├── values-sr/
│   │   │   │   │   │   │   │   │   └── values-sr.xml
│   │   │   │   │   │   │   │   ├── values-sv/
│   │   │   │   │   │   │   │   │   └── values-sv.xml
│   │   │   │   │   │   │   │   ├── values-sw/
│   │   │   │   │   │   │   │   │   └── values-sw.xml
│   │   │   │   │   │   │   │   ├── values-ta/
│   │   │   │   │   │   │   │   │   └── values-ta.xml
│   │   │   │   │   │   │   │   ├── values-te/
│   │   │   │   │   │   │   │   │   └── values-te.xml
│   │   │   │   │   │   │   │   ├── values-th/
│   │   │   │   │   │   │   │   │   └── values-th.xml
│   │   │   │   │   │   │   │   ├── values-tl/
│   │   │   │   │   │   │   │   │   └── values-tl.xml
│   │   │   │   │   │   │   │   ├── values-tr/
│   │   │   │   │   │   │   │   │   └── values-tr.xml
│   │   │   │   │   │   │   │   ├── values-uk/
│   │   │   │   │   │   │   │   │   └── values-uk.xml
│   │   │   │   │   │   │   │   ├── values-ur/
│   │   │   │   │   │   │   │   │   └── values-ur.xml
│   │   │   │   │   │   │   │   ├── values-uz/
│   │   │   │   │   │   │   │   │   └── values-uz.xml
│   │   │   │   │   │   │   │   ├── values-v21/
│   │   │   │   │   │   │   │   │   └── values-v21.xml
│   │   │   │   │   │   │   │   ├── values-vi/
│   │   │   │   │   │   │   │   │   └── values-vi.xml
│   │   │   │   │   │   │   │   ├── values-zh-rCN/
│   │   │   │   │   │   │   │   │   └── values-zh-rCN.xml
│   │   │   │   │   │   │   │   ├── values-zh-rHK/
│   │   │   │   │   │   │   │   │   └── values-zh-rHK.xml
│   │   │   │   │   │   │   │   ├── values-zh-rTW/
│   │   │   │   │   │   │   │   │   └── values-zh-rTW.xml
│   │   │   │   │   │   │   │   └── values-zu/
│   │   │   │   │   │   │   │       └── values-zu.xml
│   │   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   │   └── packageDebugAndroidTestResources/
│   │   │   │   │   │       ├── merged.dir/
│   │   │   │   │   │       ├── stripped.dir/
│   │   │   │   │   │       │   │   │   │   │   │       └── merger.xml
│   │   │   │   │   ├── debugAndroidTest-mergeJavaRes/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   │   ├── 1ZuCeLYWmYw5MfiXYYnDIw==
│   │   │   │   │   │   │   ├── 3VJ0q7BGqqlcLs7MI_dHlw==
│   │   │   │   │   │   │   ├── 7trlVWptO4XEuV+Pwn+FJg==
│   │   │   │   │   │   │   ├── eAb6b17AoioR+wuvsUlXUg==
│   │   │   │   │   │   │   ├── EKfNJDNp1qej0g_hpuBvyw==
│   │   │   │   │   │   │   ├── fbJN1jTpNAQTBDjunGpNNw==
│   │   │   │   │   │   │   ├── gMUj5N1rwbVTZNnhBOwS4w==
│   │   │   │   │   │   │   ├── i1fywGgR7739mcd2F7zSGg==
│   │   │   │   │   │   │   ├── iAPbN5fNT6HNA+q7sMeHEw==
│   │   │   │   │   │   │   ├── IO63ZVBDFar17KQf_eKhoQ==
│   │   │   │   │   │   │   ├── J7CAnpdxVb_3lbUfaCbsMg==
│   │   │   │   │   │   │   ├── jrJKw3qGpVbyDMBUg69i1A==
│   │   │   │   │   │   │   ├── kBPjyU0m7mBApb7pIiwI2Q==
│   │   │   │   │   │   │   ├── Kws7Ph2jc1f3d_ecLLw8yA==
│   │   │   │   │   │   │   ├── L+aTIiZM60Y12ESzMr+bBQ==
│   │   │   │   │   │   │   ├── m7ifgdNNq+vMZDPtmarc1Q==
│   │   │   │   │   │   │   ├── mKewgQLdnB2MM0aEYmp3EQ==
│   │   │   │   │   │   │   ├── qD5dgASdHa8TAOro2Bvrtw==
│   │   │   │   │   │   │   ├── qOLoLN95hFQC6sku5Qy+CQ==
│   │   │   │   │   │   │   ├── rrna0K8qZsqmJGvstjZKCQ==
│   │   │   │   │   │   │   ├── tL2pp5i9_KZCC65aeIVhDg==
│   │   │   │   │   │   │   └── v1FFNCoEO5or6_tvb1ZiNA==
│   │   │   │   │   │   └── merge-state
│   │   │   │   │   ├── mergeDebugAndroidTestAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugAndroidTestJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeReleaseAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeReleaseJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── packageDebugAndroidTest/tmp/debugAndroidTest/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   │   ├── androidResources
│   │   │   │   │   │   │   └── javaResources0
│   │   │   │   │   │   └── dex-renamer-state.txt
│   │   │   │   │   ├── release/packageReleaseResources/
│   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── transformDebugClassesWithAsm/
│   │   │   │   │   └── transformReleaseClassesWithAsm/
│   │   │   │   ├── java_res/
│   │   │   │   │   ├── debug/processDebugJavaRes/out/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   └── internal/
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── crypto.kotlin_module
│   │   │   │   │   └── release/processReleaseJavaRes/out/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │       │   ├── di/
│   │   │   │   │       │   └── internal/
│   │   │   │   │       └── META-INF/
│   │   │   │   │           └── crypto.kotlin_module
│   │   │   │   ├── javac/
│   │   │   │   │   ├── debug/compileDebugJavaWithJavac/classes/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/internal/
│   │   │   │   │   │   │   ├── FakeCryptoEngine_Factory$InstanceHolder.class
│   │   │   │   │   │   │   └── FakeCryptoEngine_Factory.class
│   │   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_core_crypto_di_CryptoModule.class
│   │   │   │   │   └── release/compileReleaseJavaWithJavac/classes/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/crypto/internal/
│   │   │   │   │       │   ├── FakeCryptoEngine_Factory$InstanceHolder.class
│   │   │   │   │       │   └── FakeCryptoEngine_Factory.class
│   │   │   │   │       └── hilt_aggregated_deps/
│   │   │   │   │           └── _com_miguelrodriguez19_safecube_core_crypto_di_CryptoModule.class
│   │   │   │   ├── library_and_local_jars_jni/debug/copyDebugJniLibsProjectAndLocalJars/jni/
│   │   │   │   ├── library_art_profile/
│   │   │   │   │   ├── debug/prepareDebugArtProfile/
│   │   │   │   │   └── release/prepareReleaseArtProfile/
│   │   │   │   ├── library_jni/
│   │   │   │   │   ├── debug/copyDebugJniLibsProjectOnly/jni/
│   │   │   │   │   └── release/copyReleaseJniLibsProjectOnly/jni/
│   │   │   │   ├── linked_resources_binary_format/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   ├── linked-resources-binary-format.ap_
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── lint_publish_jar/global/prepareLintJarForPublish/
│   │   │   │   ├── local_only_symbol_list/
│   │   │   │   │   ├── debug/parseDebugLocalResources/
│   │   │   │   │   │   └── R-def.txt
│   │   │   │   │   ├── debugAndroidTest/parseDebugAndroidTestLocalResources/
│   │   │   │   │   │   └── R-def.txt
│   │   │   │   │   └── release/parseReleaseLocalResources/
│   │   │   │   │       └── R-def.txt
│   │   │   │   ├── manifest_merge_blame_file/
│   │   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   │   └── manifest-merger-blame-debug-report.txt
│   │   │   │   │   ├── debugAndroidTest/processDebugAndroidTestManifest/
│   │   │   │   │   │   └── manifest-merger-blame-debug-androidTest-report.txt
│   │   │   │   │   └── release/processReleaseManifest/
│   │   │   │   │       └── manifest-merger-blame-release-report.txt
│   │   │   │   ├── merged_consumer_proguard_file/debug/mergeDebugConsumerProguardFiles/
│   │   │   │   ├── merged_java_res/
│   │   │   │   │   ├── debug/mergeDebugJavaResource/
│   │   │   │   │   │   └── feature-crypto.jar
│   │   │   │   │   └── debugAndroidTest/mergeDebugAndroidTestJavaResource/
│   │   │   │   │       └── feature-crypto.jar
│   │   │   │   ├── merged_jni_libs/
│   │   │   │   │   ├── debug/mergeDebugJniLibFolders/out/
│   │   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestJniLibFolders/out/
│   │   │   │   │   └── release/mergeReleaseJniLibFolders/out/
│   │   │   │   ├── merged_manifest/
│   │   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   │   └── AndroidManifest.xml
│   │   │   │   │   └── release/processReleaseManifest/
│   │   │   │   │       └── AndroidManifest.xml
│   │   │   │   ├── merged_res/debugAndroidTest/mergeDebugAndroidTestResources/
│   │   │   │   │   ├── values-af_values-af.arsc.flat
│   │   │   │   │   ├── values-am_values-am.arsc.flat
│   │   │   │   │   ├── values-ar_values-ar.arsc.flat
│   │   │   │   │   ├── values-as_values-as.arsc.flat
│   │   │   │   │   ├── values-az_values-az.arsc.flat
│   │   │   │   │   ├── values-b+sr+Latn_values-b+sr+Latn.arsc.flat
│   │   │   │   │   ├── values-be_values-be.arsc.flat
│   │   │   │   │   ├── values-bg_values-bg.arsc.flat
│   │   │   │   │   ├── values-bn_values-bn.arsc.flat
│   │   │   │   │   ├── values-bs_values-bs.arsc.flat
│   │   │   │   │   ├── values-ca_values-ca.arsc.flat
│   │   │   │   │   ├── values-cs_values-cs.arsc.flat
│   │   │   │   │   ├── values-da_values-da.arsc.flat
│   │   │   │   │   ├── values-de_values-de.arsc.flat
│   │   │   │   │   ├── values-el_values-el.arsc.flat
│   │   │   │   │   ├── values-en-rAU_values-en-rAU.arsc.flat
│   │   │   │   │   ├── values-en-rCA_values-en-rCA.arsc.flat
│   │   │   │   │   ├── values-en-rGB_values-en-rGB.arsc.flat
│   │   │   │   │   ├── values-en-rIN_values-en-rIN.arsc.flat
│   │   │   │   │   ├── values-en-rXC_values-en-rXC.arsc.flat
│   │   │   │   │   ├── values-es-rUS_values-es-rUS.arsc.flat
│   │   │   │   │   ├── values-es_values-es.arsc.flat
│   │   │   │   │   ├── values-et_values-et.arsc.flat
│   │   │   │   │   ├── values-eu_values-eu.arsc.flat
│   │   │   │   │   ├── values-fa_values-fa.arsc.flat
│   │   │   │   │   ├── values-fi_values-fi.arsc.flat
│   │   │   │   │   ├── values-fr-rCA_values-fr-rCA.arsc.flat
│   │   │   │   │   ├── values-fr_values-fr.arsc.flat
│   │   │   │   │   ├── values-gl_values-gl.arsc.flat
│   │   │   │   │   ├── values-gu_values-gu.arsc.flat
│   │   │   │   │   ├── values-hi_values-hi.arsc.flat
│   │   │   │   │   ├── values-hr_values-hr.arsc.flat
│   │   │   │   │   ├── values-hu_values-hu.arsc.flat
│   │   │   │   │   ├── values-hy_values-hy.arsc.flat
│   │   │   │   │   ├── values-in_values-in.arsc.flat
│   │   │   │   │   ├── values-is_values-is.arsc.flat
│   │   │   │   │   ├── values-it_values-it.arsc.flat
│   │   │   │   │   ├── values-iw_values-iw.arsc.flat
│   │   │   │   │   ├── values-ja_values-ja.arsc.flat
│   │   │   │   │   ├── values-ka_values-ka.arsc.flat
│   │   │   │   │   ├── values-kk_values-kk.arsc.flat
│   │   │   │   │   ├── values-km_values-km.arsc.flat
│   │   │   │   │   ├── values-kn_values-kn.arsc.flat
│   │   │   │   │   ├── values-ko_values-ko.arsc.flat
│   │   │   │   │   ├── values-ky_values-ky.arsc.flat
│   │   │   │   │   ├── values-lo_values-lo.arsc.flat
│   │   │   │   │   ├── values-lt_values-lt.arsc.flat
│   │   │   │   │   ├── values-lv_values-lv.arsc.flat
│   │   │   │   │   ├── values-mk_values-mk.arsc.flat
│   │   │   │   │   ├── values-ml_values-ml.arsc.flat
│   │   │   │   │   ├── values-mn_values-mn.arsc.flat
│   │   │   │   │   ├── values-mr_values-mr.arsc.flat
│   │   │   │   │   ├── values-ms_values-ms.arsc.flat
│   │   │   │   │   ├── values-my_values-my.arsc.flat
│   │   │   │   │   ├── values-nb_values-nb.arsc.flat
│   │   │   │   │   ├── values-ne_values-ne.arsc.flat
│   │   │   │   │   ├── values-nl_values-nl.arsc.flat
│   │   │   │   │   ├── values-or_values-or.arsc.flat
│   │   │   │   │   ├── values-pa_values-pa.arsc.flat
│   │   │   │   │   ├── values-pl_values-pl.arsc.flat
│   │   │   │   │   ├── values-pt-rBR_values-pt-rBR.arsc.flat
│   │   │   │   │   ├── values-pt-rPT_values-pt-rPT.arsc.flat
│   │   │   │   │   ├── values-pt_values-pt.arsc.flat
│   │   │   │   │   ├── values-ro_values-ro.arsc.flat
│   │   │   │   │   ├── values-ru_values-ru.arsc.flat
│   │   │   │   │   ├── values-si_values-si.arsc.flat
│   │   │   │   │   ├── values-sk_values-sk.arsc.flat
│   │   │   │   │   ├── values-sl_values-sl.arsc.flat
│   │   │   │   │   ├── values-sq_values-sq.arsc.flat
│   │   │   │   │   ├── values-sr_values-sr.arsc.flat
│   │   │   │   │   ├── values-sv_values-sv.arsc.flat
│   │   │   │   │   ├── values-sw_values-sw.arsc.flat
│   │   │   │   │   ├── values-ta_values-ta.arsc.flat
│   │   │   │   │   ├── values-te_values-te.arsc.flat
│   │   │   │   │   ├── values-th_values-th.arsc.flat
│   │   │   │   │   ├── values-tl_values-tl.arsc.flat
│   │   │   │   │   ├── values-tr_values-tr.arsc.flat
│   │   │   │   │   ├── values-uk_values-uk.arsc.flat
│   │   │   │   │   ├── values-ur_values-ur.arsc.flat
│   │   │   │   │   ├── values-uz_values-uz.arsc.flat
│   │   │   │   │   ├── values-v21_values-v21.arsc.flat
│   │   │   │   │   ├── values-vi_values-vi.arsc.flat
│   │   │   │   │   ├── values-zh-rCN_values-zh-rCN.arsc.flat
│   │   │   │   │   ├── values-zh-rHK_values-zh-rHK.arsc.flat
│   │   │   │   │   ├── values-zh-rTW_values-zh-rTW.arsc.flat
│   │   │   │   │   ├── values-zu_values-zu.arsc.flat
│   │   │   │   │   └── values_values.arsc.flat
│   │   │   │   ├── merged_res_blame_folder/debugAndroidTest/mergeDebugAndroidTestResources/out/multi-v2/
│   │   │   │   │   ├── mergeDebugAndroidTestResources.json
│   │   │   │   │   ├── values-af.json
│   │   │   │   │   ├── values-am.json
│   │   │   │   │   ├── values-ar.json
│   │   │   │   │   ├── values-as.json
│   │   │   │   │   ├── values-az.json
│   │   │   │   │   ├── values-b+sr+Latn.json
│   │   │   │   │   ├── values-be.json
│   │   │   │   │   ├── values-bg.json
│   │   │   │   │   ├── values-bn.json
│   │   │   │   │   ├── values-bs.json
│   │   │   │   │   ├── values-ca.json
│   │   │   │   │   ├── values-cs.json
│   │   │   │   │   ├── values-da.json
│   │   │   │   │   ├── values-de.json
│   │   │   │   │   ├── values-el.json
│   │   │   │   │   ├── values-en-rAU.json
│   │   │   │   │   ├── values-en-rCA.json
│   │   │   │   │   ├── values-en-rGB.json
│   │   │   │   │   ├── values-en-rIN.json
│   │   │   │   │   ├── values-en-rXC.json
│   │   │   │   │   ├── values-es-rUS.json
│   │   │   │   │   ├── values-es.json
│   │   │   │   │   ├── values-et.json
│   │   │   │   │   ├── values-eu.json
│   │   │   │   │   ├── values-fa.json
│   │   │   │   │   ├── values-fi.json
│   │   │   │   │   ├── values-fr-rCA.json
│   │   │   │   │   ├── values-fr.json
│   │   │   │   │   ├── values-gl.json
│   │   │   │   │   ├── values-gu.json
│   │   │   │   │   ├── values-hi.json
│   │   │   │   │   ├── values-hr.json
│   │   │   │   │   ├── values-hu.json
│   │   │   │   │   ├── values-hy.json
│   │   │   │   │   ├── values-in.json
│   │   │   │   │   ├── values-is.json
│   │   │   │   │   ├── values-it.json
│   │   │   │   │   ├── values-iw.json
│   │   │   │   │   ├── values-ja.json
│   │   │   │   │   ├── values-ka.json
│   │   │   │   │   ├── values-kk.json
│   │   │   │   │   ├── values-km.json
│   │   │   │   │   ├── values-kn.json
│   │   │   │   │   ├── values-ko.json
│   │   │   │   │   ├── values-ky.json
│   │   │   │   │   ├── values-lo.json
│   │   │   │   │   ├── values-lt.json
│   │   │   │   │   ├── values-lv.json
│   │   │   │   │   ├── values-mk.json
│   │   │   │   │   ├── values-ml.json
│   │   │   │   │   ├── values-mn.json
│   │   │   │   │   ├── values-mr.json
│   │   │   │   │   ├── values-ms.json
│   │   │   │   │   ├── values-my.json
│   │   │   │   │   ├── values-nb.json
│   │   │   │   │   ├── values-ne.json
│   │   │   │   │   ├── values-nl.json
│   │   │   │   │   ├── values-or.json
│   │   │   │   │   ├── values-pa.json
│   │   │   │   │   ├── values-pl.json
│   │   │   │   │   ├── values-pt-rBR.json
│   │   │   │   │   ├── values-pt-rPT.json
│   │   │   │   │   ├── values-pt.json
│   │   │   │   │   ├── values-ro.json
│   │   │   │   │   ├── values-ru.json
│   │   │   │   │   ├── values-si.json
│   │   │   │   │   ├── values-sk.json
│   │   │   │   │   ├── values-sl.json
│   │   │   │   │   ├── values-sq.json
│   │   │   │   │   ├── values-sr.json
│   │   │   │   │   ├── values-sv.json
│   │   │   │   │   ├── values-sw.json
│   │   │   │   │   ├── values-ta.json
│   │   │   │   │   ├── values-te.json
│   │   │   │   │   ├── values-th.json
│   │   │   │   │   ├── values-tl.json
│   │   │   │   │   ├── values-tr.json
│   │   │   │   │   ├── values-uk.json
│   │   │   │   │   ├── values-ur.json
│   │   │   │   │   ├── values-uz.json
│   │   │   │   │   ├── values-v21.json
│   │   │   │   │   ├── values-vi.json
│   │   │   │   │   ├── values-zh-rCN.json
│   │   │   │   │   ├── values-zh-rHK.json
│   │   │   │   │   ├── values-zh-rTW.json
│   │   │   │   │   ├── values-zu.json
│   │   │   │   │   └── values.json
│   │   │   │   ├── mixed_scope_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── navigation_json/
│   │   │   │   │   ├── debug/extractDeepLinksDebug/
│   │   │   │   │   │   └── navigation.json
│   │   │   │   │   └── release/extractDeepLinksRelease/
│   │   │   │   │       └── navigation.json
│   │   │   │   ├── navigation_json_for_aar/debug/extractDeepLinksForAarDebug/
│   │   │   │   ├── nested_resources_validation_report/
│   │   │   │   │   ├── debug/generateDebugResources/
│   │   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestResources/
│   │   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   │   └── release/generateReleaseResources/
│   │   │   │   │       └── nestedResourcesValidationReport.txt
│   │   │   │   ├── packaged_manifests/debugAndroidTest/processDebugAndroidTestManifest/
│   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── packaged_res/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   ├── project_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   │   ├── f65394f3cbaae1e261f4bc6e2eb4ddcd7012f629470e58707f844736c46f3d07_0.jar
│   │   │   │   │   ├── f65394f3cbaae1e261f4bc6e2eb4ddcd7012f629470e58707f844736c46f3d07_1.jar
│   │   │   │   │   ├── f65394f3cbaae1e261f4bc6e2eb4ddcd7012f629470e58707f844736c46f3d07_2.jar
│   │   │   │   │   ├── f65394f3cbaae1e261f4bc6e2eb4ddcd7012f629470e58707f844736c46f3d07_3.jar
│   │   │   │   │   ├── f65394f3cbaae1e261f4bc6e2eb4ddcd7012f629470e58707f844736c46f3d07_4.jar
│   │   │   │   │   └── f65394f3cbaae1e261f4bc6e2eb4ddcd7012f629470e58707f844736c46f3d07_5.jar
│   │   │   │   ├── public_res/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   ├── runtime_library_classes_dir/debug/bundleLibRuntimeToDirDebug/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   └── CryptoModule.class
│   │   │   │   │   │   ├── internal/
│   │   │   │   │   │   │   ├── FakeCryptoEngine.class
│   │   │   │   │   │   │   ├── FakeCryptoEngine_Factory$InstanceHolder.class
│   │   │   │   │   │   │   └── FakeCryptoEngine_Factory.class
│   │   │   │   │   │   ├── CryptoEngine.class
│   │   │   │   │   │   ├── DecryptionRequest.class
│   │   │   │   │   │   ├── EncryptionRequest.class
│   │   │   │   │   │   ├── EncryptionResult.class
│   │   │   │   │   │   ├── KdfEngine.class
│   │   │   │   │   │   ├── KdfRequest.class
│   │   │   │   │   │   ├── KeyUnwrapRequest.class
│   │   │   │   │   │   ├── KeyWrapping.class
│   │   │   │   │   │   └── KeyWrapRequest.class
│   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_crypto_di_CryptoModule.class
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── crypto.kotlin_module
│   │   │   │   ├── runtime_library_classes_jar/
│   │   │   │   │   ├── debug/bundleLibRuntimeToJarDebug/
│   │   │   │   │   │   └── classes.jar
│   │   │   │   │   └── release/bundleLibRuntimeToJarRelease/
│   │   │   │   │       └── classes.jar
│   │   │   │   ├── runtime_symbol_list/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── R.txt
│   │   │   │   ├── signing_config_versions/debugAndroidTest/writeDebugAndroidTestSigningConfigVersions/
│   │   │   │   │   └── signing-config-versions.json
│   │   │   │   ├── stable_resource_ids_file/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── stableIds.txt
│   │   │   │   ├── sub_project_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── symbol_list_with_package_name/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── package-aware-r.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── package-aware-r.txt
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── package-aware-r.txt
│   │   │   │   ├── tmp/manifest/androidTest/debug/
│   │   │   │   └── validate_signing_config/debugAndroidTest/validateSigningDebugAndroidTest/
│   │   │   ├── kotlin/
│   │   │   │   ├── compileDebugKotlin/
│   │   │   │   │   ├── cacheable/
│   │   │   │   │   │   ├── caches-jvm/
│   │   │   │   │   │   │   ├── compilerPluginFiles/
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │   │   │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │   │   │   │   ├── inputs/
│   │   │   │   │   │   │   │   ├── source-to-output.tab
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.values.at
│   │   │   │   │   │   │   │   ├── source-to-output.tab_i
│   │   │   │   │   │   │   │   └── source-to-output.tab_i.len
│   │   │   │   │   │   │   ├── jvm/
│   │   │   │   │   │   │   │   └── kotlin/
│   │   │   │   │   │   │   │       ├── class-attributes.tab
│   │   │   │   │   │   │   │       ├── class-attributes.tab.keystream
│   │   │   │   │   │   │   │       ├── class-attributes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── class-attributes.tab.len
│   │   │   │   │   │   │   │       ├── class-attributes.tab.values.at
│   │   │   │   │   │   │   │       ├── class-attributes.tab_i
│   │   │   │   │   │   │   │       ├── class-attributes.tab_i.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.values.at
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │   │   │   │   │       ├── proto.tab
│   │   │   │   │   │   │   │       ├── proto.tab.keystream
│   │   │   │   │   │   │   │       ├── proto.tab.keystream.len
│   │   │   │   │   │   │   │       ├── proto.tab.len
│   │   │   │   │   │   │   │       ├── proto.tab.values.at
│   │   │   │   │   │   │   │       ├── proto.tab_i
│   │   │   │   │   │   │   │       ├── proto.tab_i.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │   │   │   │   │       ├── source-to-classes.tab_i
│   │   │   │   │   │   │   │       ├── source-to-classes.tab_i.len
│   │   │   │   │   │   │   │       ├── subtypes.tab
│   │   │   │   │   │   │   │       ├── subtypes.tab.keystream
│   │   │   │   │   │   │   │       ├── subtypes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── subtypes.tab.len
│   │   │   │   │   │   │   │       ├── subtypes.tab.values.at
│   │   │   │   │   │   │   │       ├── subtypes.tab_i
│   │   │   │   │   │   │   │       ├── subtypes.tab_i.len
│   │   │   │   │   │   │   │       ├── supertypes.tab
│   │   │   │   │   │   │   │       ├── supertypes.tab.keystream
│   │   │   │   │   │   │   │       ├── supertypes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── supertypes.tab.len
│   │   │   │   │   │   │   │       ├── supertypes.tab.values.at
│   │   │   │   │   │   │   │       ├── supertypes.tab_i
│   │   │   │   │   │   │   │       └── supertypes.tab_i.len
│   │   │   │   │   │   │   └── lookups/
│   │   │   │   │   │   │       ├── counters.tab
│   │   │   │   │   │   │       ├── file-to-id.tab
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream.len
│   │   │   │   │   │   │       ├── file-to-id.tab.len
│   │   │   │   │   │   │       ├── file-to-id.tab.values.at
│   │   │   │   │   │   │       ├── file-to-id.tab_i
│   │   │   │   │   │   │       ├── file-to-id.tab_i.len
│   │   │   │   │   │   │       ├── id-to-file.tab
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream.len
│   │   │   │   │   │   │       ├── id-to-file.tab.len
│   │   │   │   │   │   │       ├── id-to-file.tab.values.at
│   │   │   │   │   │   │       ├── id-to-file.tab_i
│   │   │   │   │   │   │       ├── id-to-file.tab_i.len
│   │   │   │   │   │   │       ├── lookups.tab
│   │   │   │   │   │   │       ├── lookups.tab.keystream
│   │   │   │   │   │   │       ├── lookups.tab.keystream.len
│   │   │   │   │   │   │       ├── lookups.tab.len
│   │   │   │   │   │   │       ├── lookups.tab.values.at
│   │   │   │   │   │   │       ├── lookups.tab_i
│   │   │   │   │   │   │       └── lookups.tab_i.len
│   │   │   │   │   │   └── last-build.bin
│   │   │   │   │   ├── classpath-snapshot/
│   │   │   │   │   │   └── shrunk-classpath-snapshot.bin
│   │   │   │   │   └── local-state/
│   │   │   │   └── compileReleaseKotlin/
│   │   │   │       ├── cacheable/
│   │   │   │       │   ├── caches-jvm/
│   │   │   │       │   │   ├── compilerPluginFiles/
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │       │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │       │   │   ├── inputs/
│   │   │   │       │   │   │   ├── source-to-output.tab
│   │   │   │       │   │   │   ├── source-to-output.tab.keystream
│   │   │   │       │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │       │   │   │   ├── source-to-output.tab.len
│   │   │   │       │   │   │   ├── source-to-output.tab.values.at
│   │   │   │       │   │   │   ├── source-to-output.tab_i
│   │   │   │       │   │   │   └── source-to-output.tab_i.len
│   │   │   │       │   │   ├── jvm/
│   │   │   │       │   │   │   └── kotlin/
│   │   │   │       │   │   │       ├── class-attributes.tab
│   │   │   │       │   │   │       ├── class-attributes.tab.keystream
│   │   │   │       │   │   │       ├── class-attributes.tab.keystream.len
│   │   │   │       │   │   │       ├── class-attributes.tab.len
│   │   │   │       │   │   │       ├── class-attributes.tab.values.at
│   │   │   │       │   │   │       ├── class-attributes.tab_i
│   │   │   │       │   │   │       ├── class-attributes.tab_i.len
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.len
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.values.at
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab_i
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab_i.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │       │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │       │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │       │   │   │       ├── proto.tab
│   │   │   │       │   │   │       ├── proto.tab.keystream
│   │   │   │       │   │   │       ├── proto.tab.keystream.len
│   │   │   │       │   │   │       ├── proto.tab.len
│   │   │   │       │   │   │       ├── proto.tab.values.at
│   │   │   │       │   │   │       ├── proto.tab_i
│   │   │   │       │   │   │       ├── proto.tab_i.len
│   │   │   │       │   │   │       ├── source-to-classes.tab
│   │   │   │       │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │       │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │       │   │   │       ├── source-to-classes.tab.len
│   │   │   │       │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │       │   │   │       ├── source-to-classes.tab_i
│   │   │   │       │   │   │       ├── source-to-classes.tab_i.len
│   │   │   │       │   │   │       ├── subtypes.tab
│   │   │   │       │   │   │       ├── subtypes.tab.keystream
│   │   │   │       │   │   │       ├── subtypes.tab.keystream.len
│   │   │   │       │   │   │       ├── subtypes.tab.len
│   │   │   │       │   │   │       ├── subtypes.tab.values.at
│   │   │   │       │   │   │       ├── subtypes.tab_i
│   │   │   │       │   │   │       ├── subtypes.tab_i.len
│   │   │   │       │   │   │       ├── supertypes.tab
│   │   │   │       │   │   │       ├── supertypes.tab.keystream
│   │   │   │       │   │   │       ├── supertypes.tab.keystream.len
│   │   │   │       │   │   │       ├── supertypes.tab.len
│   │   │   │       │   │   │       ├── supertypes.tab.values.at
│   │   │   │       │   │   │       ├── supertypes.tab_i
│   │   │   │       │   │   │       └── supertypes.tab_i.len
│   │   │   │       │   │   └── lookups/
│   │   │   │       │   │       ├── counters.tab
│   │   │   │       │   │       ├── file-to-id.tab
│   │   │   │       │   │       ├── file-to-id.tab.keystream
│   │   │   │       │   │       ├── file-to-id.tab.keystream.len
│   │   │   │       │   │       ├── file-to-id.tab.len
│   │   │   │       │   │       ├── file-to-id.tab.values.at
│   │   │   │       │   │       ├── file-to-id.tab_i
│   │   │   │       │   │       ├── file-to-id.tab_i.len
│   │   │   │       │   │       ├── id-to-file.tab
│   │   │   │       │   │       ├── id-to-file.tab.keystream
│   │   │   │       │   │       ├── id-to-file.tab.keystream.len
│   │   │   │       │   │       ├── id-to-file.tab.len
│   │   │   │       │   │       ├── id-to-file.tab.values.at
│   │   │   │       │   │       ├── id-to-file.tab_i
│   │   │   │       │   │       ├── id-to-file.tab_i.len
│   │   │   │       │   │       ├── lookups.tab
│   │   │   │       │   │       ├── lookups.tab.keystream
│   │   │   │       │   │       ├── lookups.tab.keystream.len
│   │   │   │       │   │       ├── lookups.tab.len
│   │   │   │       │   │       ├── lookups.tab.values.at
│   │   │   │       │   │       ├── lookups.tab_i
│   │   │   │       │   │       └── lookups.tab_i.len
│   │   │   │       │   └── last-build.bin
│   │   │   │       ├── classpath-snapshot/
│   │   │   │       │   └── shrunk-classpath-snapshot.bin
│   │   │   │       └── local-state/
│   │   │   ├── kspCaches/
│   │   │   │   ├── debug/
│   │   │   │   │   ├── backups/java/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/internal/
│   │   │   │   │   │   │   └── FakeCryptoEngine_Factory.java
│   │   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_core_crypto_di_CryptoModule.java
│   │   │   │   │   ├── logs/
│   │   │   │   │   ├── symbolLookups/
│   │   │   │   │   │   ├── counters.tab
│   │   │   │   │   │   ├── file-to-id.tab
│   │   │   │   │   │   ├── file-to-id.tab.keystream
│   │   │   │   │   │   ├── file-to-id.tab.keystream.len
│   │   │   │   │   │   ├── file-to-id.tab.len
│   │   │   │   │   │   ├── file-to-id.tab.values.at
│   │   │   │   │   │   ├── file-to-id.tab_i
│   │   │   │   │   │   ├── file-to-id.tab_i.len
│   │   │   │   │   │   ├── id-to-file.tab
│   │   │   │   │   │   ├── id-to-file.tab.keystream
│   │   │   │   │   │   ├── id-to-file.tab.keystream.len
│   │   │   │   │   │   ├── id-to-file.tab.len
│   │   │   │   │   │   ├── id-to-file.tab.values.at
│   │   │   │   │   │   ├── id-to-file.tab_i
│   │   │   │   │   │   ├── id-to-file.tab_i.len
│   │   │   │   │   │   ├── lookups.tab
│   │   │   │   │   │   ├── lookups.tab.keystream
│   │   │   │   │   │   ├── lookups.tab.keystream.len
│   │   │   │   │   │   ├── lookups.tab.len
│   │   │   │   │   │   ├── lookups.tab.values.at
│   │   │   │   │   │   ├── lookups.tab_i
│   │   │   │   │   │   └── lookups.tab_i.len
│   │   │   │   │   ├── ap-classpath-entries.bin
│   │   │   │   │   ├── caches.uptodate
│   │   │   │   │   ├── classpath-entries.bin
│   │   │   │   │   ├── classpath-structure.bin
│   │   │   │   │   ├── sealed
│   │   │   │   │   ├── sourceToOutputs
│   │   │   │   │   └── symbols
│   │   │   │   └── release/
│   │   │   │       ├── backups/java/
│   │   │   │       │   ├── com/miguelrodriguez19/safecube/core/crypto/internal/
│   │   │   │       │   │   └── FakeCryptoEngine_Factory.java
│   │   │   │       │   └── hilt_aggregated_deps/
│   │   │   │       │       └── _com_miguelrodriguez19_safecube_core_crypto_di_CryptoModule.java
│   │   │   │       ├── logs/
│   │   │   │       ├── symbolLookups/
│   │   │   │       │   ├── counters.tab
│   │   │   │       │   ├── file-to-id.tab
│   │   │   │       │   ├── file-to-id.tab.keystream
│   │   │   │       │   ├── file-to-id.tab.keystream.len
│   │   │   │       │   ├── file-to-id.tab.len
│   │   │   │       │   ├── file-to-id.tab.values.at
│   │   │   │       │   ├── file-to-id.tab_i
│   │   │   │       │   ├── file-to-id.tab_i.len
│   │   │   │       │   ├── id-to-file.tab
│   │   │   │       │   ├── id-to-file.tab.keystream
│   │   │   │       │   ├── id-to-file.tab.keystream.len
│   │   │   │       │   ├── id-to-file.tab.len
│   │   │   │       │   ├── id-to-file.tab.values.at
│   │   │   │       │   ├── id-to-file.tab_i
│   │   │   │       │   ├── id-to-file.tab_i.len
│   │   │   │       │   ├── lookups.tab
│   │   │   │       │   ├── lookups.tab.keystream
│   │   │   │       │   ├── lookups.tab.keystream.len
│   │   │   │       │   ├── lookups.tab.len
│   │   │   │       │   ├── lookups.tab.values.at
│   │   │   │       │   ├── lookups.tab_i
│   │   │   │       │   └── lookups.tab_i.len
│   │   │   │       ├── ap-classpath-entries.bin
│   │   │   │       ├── caches.uptodate
│   │   │   │       ├── classpath-entries.bin
│   │   │   │       ├── classpath-structure.bin
│   │   │   │       ├── sealed
│   │   │   │       ├── sourceToOutputs
│   │   │   │       └── symbols
│   │   │   ├── outputs/
│   │   │   │   ├── aar/
│   │   │   │   │   └── crypto-debug.aar
│   │   │   │   ├── androidTest-results/connected/debug/
│   │   │   │   ├── apk/androidTest/debug/
│   │   │   │   │   ├── crypto-debug-androidTest.apk
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── code_coverage/debugAndroidTest/connected/
│   │   │   │   │   └── coverage.ec
│   │   │   │   ├── connected_android_test_additional_output/debugAndroidTest/connected/
│   │   │   │   └── logs/
│   │   │   │       ├── manifest-merger-debug-report.txt
│   │   │   │       └── manifest-merger-release-report.txt
│   │   │   ├── reports/androidTests/connected/debug/
│   │   │   │   ├── css/
│   │   │   │   │   ├── base-style.css
│   │   │   │   │   └── style.css
│   │   │   │   ├── js/
│   │   │   │   │   └── report.js
│   │   │   │   └── index.html
│   │   │   └── tmp/
│   │   │       ├── compileDebugJavaWithJavac/
│   │   │       │   ├── compileTransaction/
│   │   │       │   │   ├── backup-dir/
│   │   │       │   │   └── stash-dir/
│   │   │       │   └── previous-compilation-data.bin
│   │   │       └── compileReleaseJavaWithJavac/
│   │   │           └── previous-compilation-data.bin
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   ├── di/
│   │   │   │   │   └── CryptoModule.kt
│   │   │   │   ├── internal/
│   │   │   │   │   └── FakeCryptoEngine.kt
│   │   │   │   ├── CryptoEngine.kt
│   │   │   │   ├── DecryptionRequest.kt
│   │   │   │   ├── EncryptionRequest.kt
│   │   │   │   ├── EncryptionResult.kt
│   │   │   │   ├── KdfEngine.kt
│   │   │   │   ├── KdfRequest.kt
│   │   │   │   ├── KeyUnwrapRequest.kt
│   │   │   │   ├── KeyWrapping.kt
│   │   │   │   └── KeyWrapRequest.kt
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── network/
│   │   ├── build/
│   │   │   ├── .transforms/
│   │   │   │   ├── 0c249eec5ee6789503b754d2b7fcf9b1/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   ├── NetworkModule.dex
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideAuthControllerApiFactory.dex
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideJsonFactory$InstanceHolder.dex
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideJsonFactory.dex
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory$InstanceHolder.dex
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory.dex
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideOkHttpClientFactory.dex
│   │   │   │   │   │   │   │   └── NetworkModule_ProvideRetrofitFactory.dex
│   │   │   │   │   │   │   ├── generated/
│   │   │   │   │   │   │   │   ├── api/
│   │   │   │   │   │   │   │   │   ├── AuthControllerApi.dex
│   │   │   │   │   │   │   │   │   ├── UserProfileControllerApi.dex
│   │   │   │   │   │   │   │   │   ├── VaultControllerApi$DefaultImpls.dex
│   │   │   │   │   │   │   │   │   ├── VaultControllerApi.dex
│   │   │   │   │   │   │   │   │   └── VaultKeyMaterialControllerApi.dex
│   │   │   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   │   │   │   └── HttpBearerAuth.dex
│   │   │   │   │   │   │   │   ├── infrastructure/
│   │   │   │   │   │   │   │   │   ├── ApiClient$Companion.dex
│   │   │   │   │   │   │   │   │   ├── ApiClient.dex
│   │   │   │   │   │   │   │   │   ├── AtomicBooleanAdapter.dex
│   │   │   │   │   │   │   │   │   ├── AtomicIntegerAdapter.dex
│   │   │   │   │   │   │   │   │   ├── AtomicLongAdapter.dex
│   │   │   │   │   │   │   │   │   ├── BigDecimalAdapter.dex
│   │   │   │   │   │   │   │   │   ├── BigIntegerAdapter.dex
│   │   │   │   │   │   │   │   │   ├── CollectionFormats$CSVParams.dex
│   │   │   │   │   │   │   │   │   ├── CollectionFormats$PIPESParams.dex
│   │   │   │   │   │   │   │   │   ├── CollectionFormats$SPACEParams.dex
│   │   │   │   │   │   │   │   │   ├── CollectionFormats$SSVParams.dex
│   │   │   │   │   │   │   │   │   ├── CollectionFormats$TSVParams.dex
│   │   │   │   │   │   │   │   │   ├── CollectionFormats.dex
│   │   │   │   │   │   │   │   │   ├── LocalDateAdapter.dex
│   │   │   │   │   │   │   │   │   ├── LocalDateTimeAdapter.dex
│   │   │   │   │   │   │   │   │   ├── OffsetDateTimeAdapter.dex
│   │   │   │   │   │   │   │   │   ├── Serializer.dex
│   │   │   │   │   │   │   │   │   ├── StringBuilderAdapter.dex
│   │   │   │   │   │   │   │   │   ├── URIAdapter.dex
│   │   │   │   │   │   │   │   │   ├── URLAdapter.dex
│   │   │   │   │   │   │   │   │   └── UUIDAdapter.dex
│   │   │   │   │   │   │   │   └── model/
│   │   │   │   │   │   │   │       ├── AuthenticateAccountRequest$$serializer.dex
│   │   │   │   │   │   │   │       ├── AuthenticateAccountRequest$Companion.dex
│   │   │   │   │   │   │   │       ├── AuthenticateAccountRequest.dex
│   │   │   │   │   │   │   │       ├── AuthTokensResponse$$serializer.dex
│   │   │   │   │   │   │   │       ├── AuthTokensResponse$Companion.dex
│   │   │   │   │   │   │   │       ├── AuthTokensResponse.dex
│   │   │   │   │   │   │   │       ├── CreateSecureItemRequest$$serializer.dex
│   │   │   │   │   │   │   │       ├── CreateSecureItemRequest$Companion.dex
│   │   │   │   │   │   │   │       ├── CreateSecureItemRequest.dex
│   │   │   │   │   │   │   │       ├── CreateSecureItemResult$$serializer.dex
│   │   │   │   │   │   │   │       ├── CreateSecureItemResult$Companion.dex
│   │   │   │   │   │   │   │       ├── CreateSecureItemResult.dex
│   │   │   │   │   │   │   │       ├── CreateUserProfileRequest$$serializer.dex
│   │   │   │   │   │   │   │       ├── CreateUserProfileRequest$Companion.dex
│   │   │   │   │   │   │   │       ├── CreateUserProfileRequest.dex
│   │   │   │   │   │   │   │       ├── DeleteSecureItemResult$$serializer.dex
│   │   │   │   │   │   │   │       ├── DeleteSecureItemResult$Companion.dex
│   │   │   │   │   │   │   │       ├── DeleteSecureItemResult.dex
│   │   │   │   │   │   │   │       ├── Get400Response$$serializer.dex
│   │   │   │   │   │   │   │       ├── Get400Response$Companion.dex
│   │   │   │   │   │   │   │       ├── Get400Response.dex
│   │   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest$$serializer.dex
│   │   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest$Companion.dex
│   │   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest.dex
│   │   │   │   │   │   │   │       ├── ListSecureItemsResponse$$serializer.dex
│   │   │   │   │   │   │   │       ├── ListSecureItemsResponse$Companion.dex
│   │   │   │   │   │   │   │       ├── ListSecureItemsResponse.dex
│   │   │   │   │   │   │   │       ├── RefreshTokenRequest$$serializer.dex
│   │   │   │   │   │   │   │       ├── RefreshTokenRequest$Companion.dex
│   │   │   │   │   │   │   │       ├── RefreshTokenRequest.dex
│   │   │   │   │   │   │   │       ├── RegisterAccountRequest$$serializer.dex
│   │   │   │   │   │   │   │       ├── RegisterAccountRequest$Companion.dex
│   │   │   │   │   │   │   │       ├── RegisterAccountRequest.dex
│   │   │   │   │   │   │   │       ├── RegisterAccountResult$$serializer.dex
│   │   │   │   │   │   │   │       ├── RegisterAccountResult$Companion.dex
│   │   │   │   │   │   │   │       ├── RegisterAccountResult.dex
│   │   │   │   │   │   │   │       ├── SecureItemResponse$$serializer.dex
│   │   │   │   │   │   │   │       ├── SecureItemResponse$Companion.dex
│   │   │   │   │   │   │   │       ├── SecureItemResponse.dex
│   │   │   │   │   │   │   │       ├── SecureItemSummaryResponse$$serializer.dex
│   │   │   │   │   │   │   │       ├── SecureItemSummaryResponse$Companion.dex
│   │   │   │   │   │   │   │       ├── SecureItemSummaryResponse.dex
│   │   │   │   │   │   │   │       ├── UpdateMaster400Response$$serializer.dex
│   │   │   │   │   │   │   │       ├── UpdateMaster400Response$Companion.dex
│   │   │   │   │   │   │   │       ├── UpdateMaster400Response.dex
│   │   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest$$serializer.dex
│   │   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest$Companion.dex
│   │   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest.dex
│   │   │   │   │   │   │   │       ├── UpdateSecureItemRequest$$serializer.dex
│   │   │   │   │   │   │   │       ├── UpdateSecureItemRequest$Companion.dex
│   │   │   │   │   │   │   │       ├── UpdateSecureItemRequest.dex
│   │   │   │   │   │   │   │       ├── UpdateSecureItemResult$$serializer.dex
│   │   │   │   │   │   │   │       ├── UpdateSecureItemResult$Companion.dex
│   │   │   │   │   │   │   │       ├── UpdateSecureItemResult.dex
│   │   │   │   │   │   │   │       ├── UpdateUserProfileRequest$$serializer.dex
│   │   │   │   │   │   │   │       ├── UpdateUserProfileRequest$Companion.dex
│   │   │   │   │   │   │   │       ├── UpdateUserProfileRequest.dex
│   │   │   │   │   │   │   │       ├── UserProfileResponse$$serializer.dex
│   │   │   │   │   │   │   │       ├── UserProfileResponse$Companion.dex
│   │   │   │   │   │   │   │       ├── UserProfileResponse.dex
│   │   │   │   │   │   │   │       ├── VaultKeyMaterialResponse$$serializer.dex
│   │   │   │   │   │   │   │       ├── VaultKeyMaterialResponse$Companion.dex
│   │   │   │   │   │   │   │       └── VaultKeyMaterialResponse.dex
│   │   │   │   │   │   │   ├── AuthInterceptor.dex
│   │   │   │   │   │   │   ├── AuthInterceptor_Factory.dex
│   │   │   │   │   │   │   ├── BuildConfig.dex
│   │   │   │   │   │   │   ├── NetworkClientFactory.dex
│   │   │   │   │   │   │   ├── NetworkConfig.dex
│   │   │   │   │   │   │   ├── TokenProvider.dex
│   │   │   │   │   │   │   ├── TokenRefreshAuthenticator.dex
│   │   │   │   │   │   │   ├── TokenRefreshAuthenticator_Factory$InstanceHolder.dex
│   │   │   │   │   │   │   └── TokenRefreshAuthenticator_Factory.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_network_di_NetworkModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 100d9045d02c66de43b29d99bdc4b82e/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   └── NetworkModule.dex
│   │   │   │   │   │   │   ├── AccessTokenProvider.dex
│   │   │   │   │   │   │   ├── AuthInterceptorFactory.dex
│   │   │   │   │   │   │   ├── BuildConfig.dex
│   │   │   │   │   │   │   ├── NetworkClientFactory.dex
│   │   │   │   │   │   │   └── NetworkConfig.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_network_di_NetworkModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 115d68add910e00bbb37ec6ec3908517/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── AccessTokenProvider.dex
│   │   │   │   │   │   │   ├── AuthInterceptorFactory.dex
│   │   │   │   │   │   │   ├── BuildConfig.dex
│   │   │   │   │   │   │   ├── NetworkClientFactory.dex
│   │   │   │   │   │   │   └── NetworkConfig.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 38581485626d412b38ff26f0f50b5012/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 3aa7fb731245d98b73a986175d192aa3/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── AccessTokenProvider.dex
│   │   │   │   │   │   │   ├── AuthInterceptorFactory.dex
│   │   │   │   │   │   │   ├── BuildConfig.dex
│   │   │   │   │   │   │   ├── NetworkClientFactory.dex
│   │   │   │   │   │   │   └── NetworkConfig.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 3c4b105a9b770ed8741fc1ea3020a4b7/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── AccessTokenProvider.dex
│   │   │   │   │   │   │   ├── AuthInterceptorFactory.dex
│   │   │   │   │   │   │   ├── BuildConfig.dex
│   │   │   │   │   │   │   ├── NetworkClientFactory.dex
│   │   │   │   │   │   │   └── NetworkConfig.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 7421603901c9eba2e47c1a395ef73540/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── AccessTokenProvider.dex
│   │   │   │   │   │   │   ├── AuthInterceptorFactory.dex
│   │   │   │   │   │   │   ├── BuildConfig.dex
│   │   │   │   │   │   │   ├── NetworkClientFactory.dex
│   │   │   │   │   │   │   └── NetworkConfig.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 78e4c23b63f4ec7ac4ea57ec9e5a229b/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── AccessTokenProvider.dex
│   │   │   │   │   │   │   ├── AuthInterceptorFactory.dex
│   │   │   │   │   │   │   ├── BuildConfig.dex
│   │   │   │   │   │   │   ├── NetworkClientFactory.dex
│   │   │   │   │   │   │   └── NetworkConfig.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 97f6103e6758c8f0725f4162f421aab0/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   └── NetworkModule.dex
│   │   │   │   │   │   │   ├── AccessTokenProvider.dex
│   │   │   │   │   │   │   ├── AuthInterceptorFactory.dex
│   │   │   │   │   │   │   ├── BuildConfig.dex
│   │   │   │   │   │   │   ├── NetworkClientFactory.dex
│   │   │   │   │   │   │   └── NetworkConfig.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_network_di_NetworkModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── cf8d304c5f88a6557cbf9260965acfc1/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   ├── NetworkModule.dex
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideApiServiceFactory.dex
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideJsonFactory$InstanceHolder.dex
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideJsonFactory.dex
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory$InstanceHolder.dex
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory.dex
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideOkHttpClientFactory.dex
│   │   │   │   │   │   │   │   └── NetworkModule_ProvideRetrofitFactory.dex
│   │   │   │   │   │   │   ├── ApiService.dex
│   │   │   │   │   │   │   ├── AuthInterceptor.dex
│   │   │   │   │   │   │   ├── AuthInterceptor_Factory.dex
│   │   │   │   │   │   │   ├── BuildConfig.dex
│   │   │   │   │   │   │   ├── NetworkClientFactory.dex
│   │   │   │   │   │   │   ├── NetworkConfig.dex
│   │   │   │   │   │   │   └── TokenProvider.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_network_di_NetworkModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── d0ff2b1517f1664ee49dee7729a6b015/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── AccessTokenProvider.dex
│   │   │   │   │   │   │   ├── AuthInterceptorFactory.dex
│   │   │   │   │   │   │   ├── BuildConfig.dex
│   │   │   │   │   │   │   ├── NetworkClientFactory.dex
│   │   │   │   │   │   │   └── NetworkConfig.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   └── deb153362e7060613a4f2be02b4e326b/
│   │   │   │       ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │       │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │       │   │   ├── AccessTokenProvider.dex
│   │   │   │       │   │   ├── AuthInterceptorFactory.dex
│   │   │   │       │   │   ├── BuildConfig.dex
│   │   │   │       │   │   ├── NetworkClientFactory.dex
│   │   │   │       │   │   └── NetworkConfig.dex
│   │   │   │       │   └── desugar_graph.bin
│   │   │   │       └── results.bin
│   │   │   ├── generated/
│   │   │   │   ├── ap_generated_sources/
│   │   │   │   │   ├── debug/out/
│   │   │   │   │   ├── debugAndroidTest/out/
│   │   │   │   │   └── release/out/
│   │   │   │   ├── hilt/component_trees/debugUnitTest/
│   │   │   │   ├── ksp/
│   │   │   │   │   ├── debug/java/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideAuthControllerApiFactory.java
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideJsonFactory.java
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory.java
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideOkHttpClientFactory.java
│   │   │   │   │   │   │   │   └── NetworkModule_ProvideRetrofitFactory.java
│   │   │   │   │   │   │   ├── AuthInterceptor_Factory.java
│   │   │   │   │   │   │   └── TokenRefreshAuthenticator_Factory.java
│   │   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_core_network_di_NetworkModule.java
│   │   │   │   │   └── release/java/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │       │   ├── di/
│   │   │   │   │       │   │   ├── NetworkModule_ProvideApiServiceFactory.java
│   │   │   │   │       │   │   ├── NetworkModule_ProvideJsonFactory.java
│   │   │   │   │       │   │   ├── NetworkModule_ProvideNetworkConfigFactory.java
│   │   │   │   │       │   │   ├── NetworkModule_ProvideOkHttpClientFactory.java
│   │   │   │   │       │   │   └── NetworkModule_ProvideRetrofitFactory.java
│   │   │   │   │       │   └── AuthInterceptor_Factory.java
│   │   │   │   │       └── hilt_aggregated_deps/
│   │   │   │   │           └── _com_miguelrodriguez19_safecube_core_network_di_NetworkModule.java
│   │   │   │   ├── openapi/
│   │   │   │   │   ├── .openapi-generator/
│   │   │   │   │   │   ├── FILES
│   │   │   │   │   │   └── VERSION
│   │   │   │   │   ├── gradle/
│   │   │   │   │   │   └── wrapper/
│   │   │   │   │   │       ├── gradle-wrapper.jar
│   │   │   │   │   │       │   │   │   │   │   ├── src/main/
│   │   │   │   │   │   └── kotlin/com/miguelrodriguez19/safecube/core/network/generated/
│   │   │   │   │   │       ├── api/
│   │   │   │   │   │       │   ├── AuthControllerApi.kt
│   │   │   │   │   │       │   ├── UserProfileControllerApi.kt
│   │   │   │   │   │       │   ├── VaultControllerApi.kt
│   │   │   │   │   │       │   └── VaultKeyMaterialControllerApi.kt
│   │   │   │   │   │       ├── auth/
│   │   │   │   │   │       │   └── HttpBearerAuth.kt
│   │   │   │   │   │       ├── infrastructure/
│   │   │   │   │   │       │   ├── ApiClient.kt
│   │   │   │   │   │       │   ├── AtomicBooleanAdapter.kt
│   │   │   │   │   │       │   ├── AtomicIntegerAdapter.kt
│   │   │   │   │   │       │   ├── AtomicLongAdapter.kt
│   │   │   │   │   │       │   ├── BigDecimalAdapter.kt
│   │   │   │   │   │       │   ├── BigIntegerAdapter.kt
│   │   │   │   │   │       │   ├── CollectionFormats.kt
│   │   │   │   │   │       │   ├── LocalDateAdapter.kt
│   │   │   │   │   │       │   ├── LocalDateTimeAdapter.kt
│   │   │   │   │   │       │   ├── OffsetDateTimeAdapter.kt
│   │   │   │   │   │       │   ├── ResponseExt.kt
│   │   │   │   │   │       │   ├── Serializer.kt
│   │   │   │   │   │       │   ├── StringBuilderAdapter.kt
│   │   │   │   │   │       │   ├── URIAdapter.kt
│   │   │   │   │   │       │   ├── URLAdapter.kt
│   │   │   │   │   │       │   └── UUIDAdapter.kt
│   │   │   │   │   │       └── model/
│   │   │   │   │   │           ├── AuthenticateAccountRequest.kt
│   │   │   │   │   │           ├── AuthTokensResponse.kt
│   │   │   │   │   │           ├── CreateSecureItemRequest.kt
│   │   │   │   │   │           ├── CreateSecureItemResult.kt
│   │   │   │   │   │           ├── CreateUserProfileRequest.kt
│   │   │   │   │   │           ├── DeleteSecureItemResult.kt
│   │   │   │   │   │           ├── Get400Response.kt
│   │   │   │   │   │           ├── InitVaultKeyMaterialRequest.kt
│   │   │   │   │   │           ├── ListSecureItemsResponse.kt
│   │   │   │   │   │           ├── RefreshTokenRequest.kt
│   │   │   │   │   │           ├── RegisterAccountRequest.kt
│   │   │   │   │   │           ├── RegisterAccountResult.kt
│   │   │   │   │   │           ├── SecureItemResponse.kt
│   │   │   │   │   │           ├── SecureItemSummaryResponse.kt
│   │   │   │   │   │           ├── UpdateMaster400Response.kt
│   │   │   │   │   │           ├── UpdateMasterWrappedKekRequest.kt
│   │   │   │   │   │           ├── UpdateSecureItemRequest.kt
│   │   │   │   │   │           ├── UpdateSecureItemResult.kt
│   │   │   │   │   │           ├── UpdateUserProfileRequest.kt
│   │   │   │   │   │           ├── UserProfileResponse.kt
│   │   │   │   │   │           └── VaultKeyMaterialResponse.kt
│   │   │   │   │   ├── .openapi-generator-ignore
│   │   │   │   │   ├── build.gradle
│   │   │   │   │   ├── gradlew
│   │   │   │   │   ├── gradlew.bat
│   │   │   │   │   ├── proguard-rules.pro
│   │   │   │   │   ├── README.md
│   │   │   │   │   └── settings.gradle
│   │   │   │   ├── res/pngs/
│   │   │   │   │   ├── debug/
│   │   │   │   │   └── release/
│   │   │   │   ├── source/buildConfig/
│   │   │   │   │   ├── androidTest/debug/com/miguelrodriguez19/safecube/core/network/test/
│   │   │   │   │   │   └── BuildConfig.java
│   │   │   │   │   ├── debug/com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   └── BuildConfig.java
│   │   │   │   │   └── release/com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │       └── BuildConfig.java
│   │   │   │   └── updated_navigation_xml/
│   │   │   │       ├── debug/
│   │   │   │       ├── debugAndroidTest/
│   │   │   │       └── release/
│   │   │   ├── intermediates/
│   │   │   │   ├── aapt_friendly_merged_manifests/
│   │   │   │   │   ├── debug/processDebugManifest/aapt/
│   │   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   │   └── output-metadata.json
│   │   │   │   │   └── release/processReleaseManifest/aapt/
│   │   │   │   │       ├── AndroidManifest.xml
│   │   │   │   │       └── output-metadata.json
│   │   │   │   ├── aar_libs_directory/debug/syncDebugLibJars/libs/
│   │   │   │   ├── aar_main_jar/debug/syncDebugLibJars/
│   │   │   │   │   └── classes.jar
│   │   │   │   ├── aar_metadata/
│   │   │   │   │   ├── debug/writeDebugAarMetadata/
│   │   │   │   │   │   │   │   │   │   │   └── release/writeReleaseAarMetadata/
│   │   │   │   │       │   │   │   │   ├── aar_metadata_check/
│   │   │   │   │   ├── debug/checkDebugAarMetadata/
│   │   │   │   │   └── debugAndroidTest/checkDebugAndroidTestAarMetadata/
│   │   │   │   ├── android_res_source_set_path_map/debugAndroidTest/mapDebugAndroidTestSourceSetPaths/
│   │   │   │   │   └── file-map.txt
│   │   │   │   ├── annotation_processor_list/
│   │   │   │   │   ├── debug/javaPreCompileDebug/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   ├── debugAndroidTest/javaPreCompileDebugAndroidTest/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   ├── debugUnitTest/javaPreCompileDebugUnitTest/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   └── release/javaPreCompileRelease/
│   │   │   │   │       └── annotationProcessors.json
│   │   │   │   ├── annotations_typedef_file/debug/extractDebugAnnotations/
│   │   │   │   │   └── typedefs.txt
│   │   │   │   ├── annotations_zip/debug/extractDebugAnnotations/
│   │   │   │   ├── apk_ide_redirect_file/debugAndroidTest/createDebugAndroidTestApkListingFileRedirect/
│   │   │   │   │   └── redirect.txt
│   │   │   │   ├── assets/
│   │   │   │   │   ├── debug/mergeDebugAssets/
│   │   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestAssets/
│   │   │   │   │   │   └── PublicSuffixDatabase.list
│   │   │   │   │   └── release/mergeReleaseAssets/
│   │   │   │   ├── built_in_kotlinc/
│   │   │   │   │   ├── debug/compileDebugKotlin/classes/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   └── NetworkModule.class
│   │   │   │   │   │   │   ├── generated/
│   │   │   │   │   │   │   │   ├── api/
│   │   │   │   │   │   │   │   │   ├── AuthControllerApi.class
│   │   │   │   │   │   │   │   │   ├── UserProfileControllerApi.class
│   │   │   │   │   │   │   │   │   ├── VaultControllerApi$DefaultImpls.class
│   │   │   │   │   │   │   │   │   ├── VaultControllerApi.class
│   │   │   │   │   │   │   │   │   └── VaultKeyMaterialControllerApi.class
│   │   │   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   │   │   │   └── HttpBearerAuth.class
│   │   │   │   │   │   │   │   ├── infrastructure/
│   │   │   │   │   │   │   │   │   ├── ApiClient$Companion.class
│   │   │   │   │   │   │   │   │   ├── ApiClient.class
│   │   │   │   │   │   │   │   │   ├── AtomicBooleanAdapter.class
│   │   │   │   │   │   │   │   │   ├── AtomicIntegerAdapter.class
│   │   │   │   │   │   │   │   │   ├── AtomicLongAdapter.class
│   │   │   │   │   │   │   │   │   ├── BigDecimalAdapter.class
│   │   │   │   │   │   │   │   │   ├── BigIntegerAdapter.class
│   │   │   │   │   │   │   │   │   ├── CollectionFormats$CSVParams.class
│   │   │   │   │   │   │   │   │   ├── CollectionFormats$PIPESParams.class
│   │   │   │   │   │   │   │   │   ├── CollectionFormats$SPACEParams.class
│   │   │   │   │   │   │   │   │   ├── CollectionFormats$SSVParams.class
│   │   │   │   │   │   │   │   │   ├── CollectionFormats$TSVParams.class
│   │   │   │   │   │   │   │   │   ├── CollectionFormats.class
│   │   │   │   │   │   │   │   │   ├── LocalDateAdapter.class
│   │   │   │   │   │   │   │   │   ├── LocalDateTimeAdapter.class
│   │   │   │   │   │   │   │   │   ├── OffsetDateTimeAdapter.class
│   │   │   │   │   │   │   │   │   ├── Serializer.class
│   │   │   │   │   │   │   │   │   ├── StringBuilderAdapter.class
│   │   │   │   │   │   │   │   │   ├── URIAdapter.class
│   │   │   │   │   │   │   │   │   ├── URLAdapter.class
│   │   │   │   │   │   │   │   │   └── UUIDAdapter.class
│   │   │   │   │   │   │   │   └── model/
│   │   │   │   │   │   │   │       ├── AuthenticateAccountRequest$$serializer.class
│   │   │   │   │   │   │   │       ├── AuthenticateAccountRequest$Companion.class
│   │   │   │   │   │   │   │       ├── AuthenticateAccountRequest.class
│   │   │   │   │   │   │   │       ├── AuthTokensResponse$$serializer.class
│   │   │   │   │   │   │   │       ├── AuthTokensResponse$Companion.class
│   │   │   │   │   │   │   │       ├── AuthTokensResponse.class
│   │   │   │   │   │   │   │       ├── CreateSecureItemRequest$$serializer.class
│   │   │   │   │   │   │   │       ├── CreateSecureItemRequest$Companion.class
│   │   │   │   │   │   │   │       ├── CreateSecureItemRequest.class
│   │   │   │   │   │   │   │       ├── CreateSecureItemResult$$serializer.class
│   │   │   │   │   │   │   │       ├── CreateSecureItemResult$Companion.class
│   │   │   │   │   │   │   │       ├── CreateSecureItemResult.class
│   │   │   │   │   │   │   │       ├── CreateUserProfileRequest$$serializer.class
│   │   │   │   │   │   │   │       ├── CreateUserProfileRequest$Companion.class
│   │   │   │   │   │   │   │       ├── CreateUserProfileRequest.class
│   │   │   │   │   │   │   │       ├── DeleteSecureItemResult$$serializer.class
│   │   │   │   │   │   │   │       ├── DeleteSecureItemResult$Companion.class
│   │   │   │   │   │   │   │       ├── DeleteSecureItemResult.class
│   │   │   │   │   │   │   │       ├── Get400Response$$serializer.class
│   │   │   │   │   │   │   │       ├── Get400Response$Companion.class
│   │   │   │   │   │   │   │       ├── Get400Response.class
│   │   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest$$serializer.class
│   │   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest$Companion.class
│   │   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest.class
│   │   │   │   │   │   │   │       ├── ListSecureItemsResponse$$serializer.class
│   │   │   │   │   │   │   │       ├── ListSecureItemsResponse$Companion.class
│   │   │   │   │   │   │   │       ├── ListSecureItemsResponse.class
│   │   │   │   │   │   │   │       ├── RefreshTokenRequest$$serializer.class
│   │   │   │   │   │   │   │       ├── RefreshTokenRequest$Companion.class
│   │   │   │   │   │   │   │       ├── RefreshTokenRequest.class
│   │   │   │   │   │   │   │       ├── RegisterAccountRequest$$serializer.class
│   │   │   │   │   │   │   │       ├── RegisterAccountRequest$Companion.class
│   │   │   │   │   │   │   │       ├── RegisterAccountRequest.class
│   │   │   │   │   │   │   │       ├── RegisterAccountResult$$serializer.class
│   │   │   │   │   │   │   │       ├── RegisterAccountResult$Companion.class
│   │   │   │   │   │   │   │       ├── RegisterAccountResult.class
│   │   │   │   │   │   │   │       ├── SecureItemResponse$$serializer.class
│   │   │   │   │   │   │   │       ├── SecureItemResponse$Companion.class
│   │   │   │   │   │   │   │       ├── SecureItemResponse.class
│   │   │   │   │   │   │   │       ├── SecureItemSummaryResponse$$serializer.class
│   │   │   │   │   │   │   │       ├── SecureItemSummaryResponse$Companion.class
│   │   │   │   │   │   │   │       ├── SecureItemSummaryResponse.class
│   │   │   │   │   │   │   │       ├── UpdateMaster400Response$$serializer.class
│   │   │   │   │   │   │   │       ├── UpdateMaster400Response$Companion.class
│   │   │   │   │   │   │   │       ├── UpdateMaster400Response.class
│   │   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest$$serializer.class
│   │   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest$Companion.class
│   │   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest.class
│   │   │   │   │   │   │   │       ├── UpdateSecureItemRequest$$serializer.class
│   │   │   │   │   │   │   │       ├── UpdateSecureItemRequest$Companion.class
│   │   │   │   │   │   │   │       ├── UpdateSecureItemRequest.class
│   │   │   │   │   │   │   │       ├── UpdateSecureItemResult$$serializer.class
│   │   │   │   │   │   │   │       ├── UpdateSecureItemResult$Companion.class
│   │   │   │   │   │   │   │       ├── UpdateSecureItemResult.class
│   │   │   │   │   │   │   │       ├── UpdateUserProfileRequest$$serializer.class
│   │   │   │   │   │   │   │       ├── UpdateUserProfileRequest$Companion.class
│   │   │   │   │   │   │   │       ├── UpdateUserProfileRequest.class
│   │   │   │   │   │   │   │       ├── UserProfileResponse$$serializer.class
│   │   │   │   │   │   │   │       ├── UserProfileResponse$Companion.class
│   │   │   │   │   │   │   │       ├── UserProfileResponse.class
│   │   │   │   │   │   │   │       ├── VaultKeyMaterialResponse$$serializer.class
│   │   │   │   │   │   │   │       ├── VaultKeyMaterialResponse$Companion.class
│   │   │   │   │   │   │   │       └── VaultKeyMaterialResponse.class
│   │   │   │   │   │   │   ├── AuthInterceptor.class
│   │   │   │   │   │   │   ├── NetworkClientFactory.class
│   │   │   │   │   │   │   ├── NetworkConfig.class
│   │   │   │   │   │   │   ├── TokenProvider.class
│   │   │   │   │   │   │   └── TokenRefreshAuthenticator.class
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── network.kotlin_module
│   │   │   │   │   ├── debugUnitTest/compileDebugUnitTestKotlin/classes/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── NetworkClientFactoryTest.class
│   │   │   │   │   │   │   ├── PingApi.class
│   │   │   │   │   │   │   ├── PingResponse$$serializer.class
│   │   │   │   │   │   │   ├── PingResponse$Companion.class
│   │   │   │   │   │   │   └── PingResponse.class
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── network.kotlin_module
│   │   │   │   │   └── release/compileReleaseKotlin/classes/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │       │   ├── di/
│   │   │   │   │       │   │   └── NetworkModule.class
│   │   │   │   │       │   ├── ApiService.class
│   │   │   │   │       │   ├── AuthInterceptor.class
│   │   │   │   │       │   ├── NetworkClientFactory.class
│   │   │   │   │       │   ├── NetworkConfig.class
│   │   │   │   │       │   └── TokenProvider.class
│   │   │   │   │       └── META-INF/
│   │   │   │   │           └── network.kotlin_module
│   │   │   │   ├── classes/
│   │   │   │   │   ├── debug/transformDebugClassesWithAsm/
│   │   │   │   │   │   ├── dirs/
│   │   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   │   ├── NetworkModule.class
│   │   │   │   │   │   │   │   │   ├── NetworkModule_ProvideAuthControllerApiFactory.class
│   │   │   │   │   │   │   │   │   ├── NetworkModule_ProvideJsonFactory$InstanceHolder.class
│   │   │   │   │   │   │   │   │   ├── NetworkModule_ProvideJsonFactory.class
│   │   │   │   │   │   │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory$InstanceHolder.class
│   │   │   │   │   │   │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory.class
│   │   │   │   │   │   │   │   │   ├── NetworkModule_ProvideOkHttpClientFactory.class
│   │   │   │   │   │   │   │   │   └── NetworkModule_ProvideRetrofitFactory.class
│   │   │   │   │   │   │   │   ├── generated/
│   │   │   │   │   │   │   │   │   ├── api/
│   │   │   │   │   │   │   │   │   │   ├── AuthControllerApi.class
│   │   │   │   │   │   │   │   │   │   ├── UserProfileControllerApi.class
│   │   │   │   │   │   │   │   │   │   ├── VaultControllerApi$DefaultImpls.class
│   │   │   │   │   │   │   │   │   │   ├── VaultControllerApi.class
│   │   │   │   │   │   │   │   │   │   └── VaultKeyMaterialControllerApi.class
│   │   │   │   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   │   │   │   │   └── HttpBearerAuth.class
│   │   │   │   │   │   │   │   │   ├── infrastructure/
│   │   │   │   │   │   │   │   │   │   ├── ApiClient$Companion.class
│   │   │   │   │   │   │   │   │   │   ├── ApiClient.class
│   │   │   │   │   │   │   │   │   │   ├── AtomicBooleanAdapter.class
│   │   │   │   │   │   │   │   │   │   ├── AtomicIntegerAdapter.class
│   │   │   │   │   │   │   │   │   │   ├── AtomicLongAdapter.class
│   │   │   │   │   │   │   │   │   │   ├── BigDecimalAdapter.class
│   │   │   │   │   │   │   │   │   │   ├── BigIntegerAdapter.class
│   │   │   │   │   │   │   │   │   │   ├── CollectionFormats$CSVParams.class
│   │   │   │   │   │   │   │   │   │   ├── CollectionFormats$PIPESParams.class
│   │   │   │   │   │   │   │   │   │   ├── CollectionFormats$SPACEParams.class
│   │   │   │   │   │   │   │   │   │   ├── CollectionFormats$SSVParams.class
│   │   │   │   │   │   │   │   │   │   ├── CollectionFormats$TSVParams.class
│   │   │   │   │   │   │   │   │   │   ├── CollectionFormats.class
│   │   │   │   │   │   │   │   │   │   ├── LocalDateAdapter.class
│   │   │   │   │   │   │   │   │   │   ├── LocalDateTimeAdapter.class
│   │   │   │   │   │   │   │   │   │   ├── OffsetDateTimeAdapter.class
│   │   │   │   │   │   │   │   │   │   ├── Serializer.class
│   │   │   │   │   │   │   │   │   │   ├── StringBuilderAdapter.class
│   │   │   │   │   │   │   │   │   │   ├── URIAdapter.class
│   │   │   │   │   │   │   │   │   │   ├── URLAdapter.class
│   │   │   │   │   │   │   │   │   │   └── UUIDAdapter.class
│   │   │   │   │   │   │   │   │   └── model/
│   │   │   │   │   │   │   │   │       ├── AuthenticateAccountRequest$$serializer.class
│   │   │   │   │   │   │   │   │       ├── AuthenticateAccountRequest$Companion.class
│   │   │   │   │   │   │   │   │       ├── AuthenticateAccountRequest.class
│   │   │   │   │   │   │   │   │       ├── AuthTokensResponse$$serializer.class
│   │   │   │   │   │   │   │   │       ├── AuthTokensResponse$Companion.class
│   │   │   │   │   │   │   │   │       ├── AuthTokensResponse.class
│   │   │   │   │   │   │   │   │       ├── CreateSecureItemRequest$$serializer.class
│   │   │   │   │   │   │   │   │       ├── CreateSecureItemRequest$Companion.class
│   │   │   │   │   │   │   │   │       ├── CreateSecureItemRequest.class
│   │   │   │   │   │   │   │   │       ├── CreateSecureItemResult$$serializer.class
│   │   │   │   │   │   │   │   │       ├── CreateSecureItemResult$Companion.class
│   │   │   │   │   │   │   │   │       ├── CreateSecureItemResult.class
│   │   │   │   │   │   │   │   │       ├── CreateUserProfileRequest$$serializer.class
│   │   │   │   │   │   │   │   │       ├── CreateUserProfileRequest$Companion.class
│   │   │   │   │   │   │   │   │       ├── CreateUserProfileRequest.class
│   │   │   │   │   │   │   │   │       ├── DeleteSecureItemResult$$serializer.class
│   │   │   │   │   │   │   │   │       ├── DeleteSecureItemResult$Companion.class
│   │   │   │   │   │   │   │   │       ├── DeleteSecureItemResult.class
│   │   │   │   │   │   │   │   │       ├── Get400Response$$serializer.class
│   │   │   │   │   │   │   │   │       ├── Get400Response$Companion.class
│   │   │   │   │   │   │   │   │       ├── Get400Response.class
│   │   │   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest$$serializer.class
│   │   │   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest$Companion.class
│   │   │   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest.class
│   │   │   │   │   │   │   │   │       ├── ListSecureItemsResponse$$serializer.class
│   │   │   │   │   │   │   │   │       ├── ListSecureItemsResponse$Companion.class
│   │   │   │   │   │   │   │   │       ├── ListSecureItemsResponse.class
│   │   │   │   │   │   │   │   │       ├── RefreshTokenRequest$$serializer.class
│   │   │   │   │   │   │   │   │       ├── RefreshTokenRequest$Companion.class
│   │   │   │   │   │   │   │   │       ├── RefreshTokenRequest.class
│   │   │   │   │   │   │   │   │       ├── RegisterAccountRequest$$serializer.class
│   │   │   │   │   │   │   │   │       ├── RegisterAccountRequest$Companion.class
│   │   │   │   │   │   │   │   │       ├── RegisterAccountRequest.class
│   │   │   │   │   │   │   │   │       ├── RegisterAccountResult$$serializer.class
│   │   │   │   │   │   │   │   │       ├── RegisterAccountResult$Companion.class
│   │   │   │   │   │   │   │   │       ├── RegisterAccountResult.class
│   │   │   │   │   │   │   │   │       ├── SecureItemResponse$$serializer.class
│   │   │   │   │   │   │   │   │       ├── SecureItemResponse$Companion.class
│   │   │   │   │   │   │   │   │       ├── SecureItemResponse.class
│   │   │   │   │   │   │   │   │       ├── SecureItemSummaryResponse$$serializer.class
│   │   │   │   │   │   │   │   │       ├── SecureItemSummaryResponse$Companion.class
│   │   │   │   │   │   │   │   │       ├── SecureItemSummaryResponse.class
│   │   │   │   │   │   │   │   │       ├── UpdateMaster400Response$$serializer.class
│   │   │   │   │   │   │   │   │       ├── UpdateMaster400Response$Companion.class
│   │   │   │   │   │   │   │   │       ├── UpdateMaster400Response.class
│   │   │   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest$$serializer.class
│   │   │   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest$Companion.class
│   │   │   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest.class
│   │   │   │   │   │   │   │   │       ├── UpdateSecureItemRequest$$serializer.class
│   │   │   │   │   │   │   │   │       ├── UpdateSecureItemRequest$Companion.class
│   │   │   │   │   │   │   │   │       ├── UpdateSecureItemRequest.class
│   │   │   │   │   │   │   │   │       ├── UpdateSecureItemResult$$serializer.class
│   │   │   │   │   │   │   │   │       ├── UpdateSecureItemResult$Companion.class
│   │   │   │   │   │   │   │   │       ├── UpdateSecureItemResult.class
│   │   │   │   │   │   │   │   │       ├── UpdateUserProfileRequest$$serializer.class
│   │   │   │   │   │   │   │   │       ├── UpdateUserProfileRequest$Companion.class
│   │   │   │   │   │   │   │   │       ├── UpdateUserProfileRequest.class
│   │   │   │   │   │   │   │   │       ├── UserProfileResponse$$serializer.class
│   │   │   │   │   │   │   │   │       ├── UserProfileResponse$Companion.class
│   │   │   │   │   │   │   │   │       ├── UserProfileResponse.class
│   │   │   │   │   │   │   │   │       ├── VaultKeyMaterialResponse$$serializer.class
│   │   │   │   │   │   │   │   │       ├── VaultKeyMaterialResponse$Companion.class
│   │   │   │   │   │   │   │   │       └── VaultKeyMaterialResponse.class
│   │   │   │   │   │   │   │   ├── AuthInterceptor.class
│   │   │   │   │   │   │   │   ├── AuthInterceptor_Factory.class
│   │   │   │   │   │   │   │   ├── BuildConfig.class
│   │   │   │   │   │   │   │   ├── NetworkClientFactory.class
│   │   │   │   │   │   │   │   ├── NetworkConfig.class
│   │   │   │   │   │   │   │   ├── TokenProvider.class
│   │   │   │   │   │   │   │   ├── TokenRefreshAuthenticator.class
│   │   │   │   │   │   │   │   ├── TokenRefreshAuthenticator_Factory$InstanceHolder.class
│   │   │   │   │   │   │   │   └── TokenRefreshAuthenticator_Factory.class
│   │   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_network_di_NetworkModule.class
│   │   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │   │       └── network.kotlin_module
│   │   │   │   │   │   └── jars/
│   │   │   │   │   ├── debugUnitTest/transformDebugUnitTestClassesWithAsm/
│   │   │   │   │   │   ├── dirs/
│   │   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   │   ├── NetworkClientFactoryTest.class
│   │   │   │   │   │   │   │   ├── PingApi.class
│   │   │   │   │   │   │   │   ├── PingResponse$$serializer.class
│   │   │   │   │   │   │   │   ├── PingResponse$Companion.class
│   │   │   │   │   │   │   │   └── PingResponse.class
│   │   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │   │       └── network.kotlin_module
│   │   │   │   │   │   └── jars/
│   │   │   │   │   └── release/transformReleaseClassesWithAsm/
│   │   │   │   │       ├── dirs/
│   │   │   │   │       │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │       │   │   ├── di/
│   │   │   │   │       │   │   │   ├── NetworkModule.class
│   │   │   │   │       │   │   │   ├── NetworkModule_ProvideApiServiceFactory.class
│   │   │   │   │       │   │   │   ├── NetworkModule_ProvideJsonFactory$InstanceHolder.class
│   │   │   │   │       │   │   │   ├── NetworkModule_ProvideJsonFactory.class
│   │   │   │   │       │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory$InstanceHolder.class
│   │   │   │   │       │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory.class
│   │   │   │   │       │   │   │   ├── NetworkModule_ProvideOkHttpClientFactory.class
│   │   │   │   │       │   │   │   └── NetworkModule_ProvideRetrofitFactory.class
│   │   │   │   │       │   │   ├── ApiService.class
│   │   │   │   │       │   │   ├── AuthInterceptor.class
│   │   │   │   │       │   │   ├── AuthInterceptor_Factory.class
│   │   │   │   │       │   │   ├── BuildConfig.class
│   │   │   │   │       │   │   ├── NetworkClientFactory.class
│   │   │   │   │       │   │   ├── NetworkConfig.class
│   │   │   │   │       │   │   └── TokenProvider.class
│   │   │   │   │       │   ├── hilt_aggregated_deps/
│   │   │   │   │       │   │   └── _com_miguelrodriguez19_safecube_core_network_di_NetworkModule.class
│   │   │   │   │       │   └── META-INF/
│   │   │   │   │       │       └── network.kotlin_module
│   │   │   │   │       └── jars/
│   │   │   │   ├── compile_and_runtime_r_class_jar/
│   │   │   │   │   ├── debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   └── debugUnitTest/generateDebugUnitTestStubRFile/
│   │   │   │   │       └── R.jar
│   │   │   │   ├── compile_library_classes_jar/
│   │   │   │   │   ├── debug/bundleLibCompileToJarDebug/
│   │   │   │   │   │   └── classes.jar
│   │   │   │   │   └── release/bundleLibCompileToJarRelease/
│   │   │   │   │       └── classes.jar
│   │   │   │   ├── compile_r_class_jar/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── R.jar
│   │   │   │   ├── compile_symbol_list/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── R.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── R.txt
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── R.txt
│   │   │   │   ├── compiled_local_resources/debug/compileDebugLibraryResources/out/
│   │   │   │   ├── compiled_navigation_res/debugAndroidTest/compileDebugAndroidTestNavigationResources/
│   │   │   │   ├── compressed_assets/debugAndroidTest/compressDebugAndroidTestAssets/out/assets/
│   │   │   │   │   └── PublicSuffixDatabase.list.jar
│   │   │   │   ├── consumer_proguard_dir/release/
│   │   │   │   ├── data_binding_layout_info_type_merge/debugAndroidTest/mergeDebugAndroidTestResources/out/
│   │   │   │   ├── data_binding_layout_info_type_package/
│   │   │   │   │   ├── debug/packageDebugResources/out/
│   │   │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/out/
│   │   │   │   │   └── release/packageReleaseResources/out/
│   │   │   │   ├── desugar_graph/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   │   ├── currentProject/
│   │   │   │   │   │   ├── dirs_bucket_0/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── dirs_bucket_1/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── dirs_bucket_2/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── dirs_bucket_3/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── dirs_bucket_4/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── dirs_bucket_5/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_79f84d91490f61805aac91c9321e9bfce59d665d92cf111672178ee4d9f83938_bucket_0/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_79f84d91490f61805aac91c9321e9bfce59d665d92cf111672178ee4d9f83938_bucket_1/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_79f84d91490f61805aac91c9321e9bfce59d665d92cf111672178ee4d9f83938_bucket_2/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_79f84d91490f61805aac91c9321e9bfce59d665d92cf111672178ee4d9f83938_bucket_3/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_79f84d91490f61805aac91c9321e9bfce59d665d92cf111672178ee4d9f83938_bucket_4/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   └── jar_79f84d91490f61805aac91c9321e9bfce59d665d92cf111672178ee4d9f83938_bucket_5/
│   │   │   │   │   │       └── graph.bin
│   │   │   │   │   ├── externalLibs/
│   │   │   │   │   ├── mixedScopes/
│   │   │   │   │   └── otherProjects/
│   │   │   │   ├── dex/debugAndroidTest/
│   │   │   │   │   ├── mergeExtDexDebugAndroidTest/
│   │   │   │   │   │   └── classes.dex
│   │   │   │   │   ├── mergeLibDexDebugAndroidTest/
│   │   │   │   │   │   ├── 0/
│   │   │   │   │   │   ├── 1/
│   │   │   │   │   │   ├── 10/
│   │   │   │   │   │   ├── 11/
│   │   │   │   │   │   ├── 12/
│   │   │   │   │   │   ├── 13/
│   │   │   │   │   │   ├── 14/
│   │   │   │   │   │   ├── 15/
│   │   │   │   │   │   ├── 2/
│   │   │   │   │   │   ├── 3/
│   │   │   │   │   │   ├── 4/
│   │   │   │   │   │   ├── 5/
│   │   │   │   │   │   ├── 6/
│   │   │   │   │   │   ├── 7/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 8/
│   │   │   │   │   │   └── 9/
│   │   │   │   │   └── mergeProjectDexDebugAndroidTest/
│   │   │   │   │       ├── 0/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 1/
│   │   │   │   │       ├── 10/
│   │   │   │   │       ├── 11/
│   │   │   │   │       ├── 12/
│   │   │   │   │       ├── 13/
│   │   │   │   │       ├── 14/
│   │   │   │   │       ├── 15/
│   │   │   │   │       ├── 2/
│   │   │   │   │       ├── 3/
│   │   │   │   │       ├── 4/
│   │   │   │   │       ├── 5/
│   │   │   │   │       ├── 6/
│   │   │   │   │       ├── 7/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 8/
│   │   │   │   │       └── 9/
│   │   │   │   ├── dex_archive_input_jar_hashes/debugAndroidTest/dexBuilderDebugAndroidTest/
│   │   │   │   │   └── out
│   │   │   │   ├── dex_number_of_buckets_file/debugAndroidTest/dexBuilderDebugAndroidTest/
│   │   │   │   │   └── out
│   │   │   │   ├── duplicate_classes_check/debugAndroidTest/checkDebugAndroidTestDuplicateClasses/
│   │   │   │   ├── external_file_lib_dex_archives/debugAndroidTest/desugarDebugAndroidTestFileDependencies/
│   │   │   │   ├── external_libs_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── external_libs_dex_archive_with_artifact_transforms/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── generated_proguard_file/
│   │   │   │   │   ├── debug/mergeDebugGeneratedProguardFiles/
│   │   │   │   │   └── release/mergeReleaseGeneratedProguardFiles/
│   │   │   │   ├── hilt/copy/debugUnitTest/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   ├── NetworkModule.class
│   │   │   │   │   │   │   ├── NetworkModule_ProvideAuthControllerApiFactory.class
│   │   │   │   │   │   │   ├── NetworkModule_ProvideJsonFactory$InstanceHolder.class
│   │   │   │   │   │   │   ├── NetworkModule_ProvideJsonFactory.class
│   │   │   │   │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory$InstanceHolder.class
│   │   │   │   │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory.class
│   │   │   │   │   │   │   ├── NetworkModule_ProvideOkHttpClientFactory.class
│   │   │   │   │   │   │   └── NetworkModule_ProvideRetrofitFactory.class
│   │   │   │   │   │   ├── generated/
│   │   │   │   │   │   │   ├── api/
│   │   │   │   │   │   │   │   ├── AuthControllerApi.class
│   │   │   │   │   │   │   │   ├── UserProfileControllerApi.class
│   │   │   │   │   │   │   │   ├── VaultControllerApi$DefaultImpls.class
│   │   │   │   │   │   │   │   ├── VaultControllerApi.class
│   │   │   │   │   │   │   │   └── VaultKeyMaterialControllerApi.class
│   │   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   │   │   └── HttpBearerAuth.class
│   │   │   │   │   │   │   ├── infrastructure/
│   │   │   │   │   │   │   │   ├── ApiClient$Companion.class
│   │   │   │   │   │   │   │   ├── ApiClient.class
│   │   │   │   │   │   │   │   ├── AtomicBooleanAdapter.class
│   │   │   │   │   │   │   │   ├── AtomicIntegerAdapter.class
│   │   │   │   │   │   │   │   ├── AtomicLongAdapter.class
│   │   │   │   │   │   │   │   ├── BigDecimalAdapter.class
│   │   │   │   │   │   │   │   ├── BigIntegerAdapter.class
│   │   │   │   │   │   │   │   ├── CollectionFormats$CSVParams.class
│   │   │   │   │   │   │   │   ├── CollectionFormats$PIPESParams.class
│   │   │   │   │   │   │   │   ├── CollectionFormats$SPACEParams.class
│   │   │   │   │   │   │   │   ├── CollectionFormats$SSVParams.class
│   │   │   │   │   │   │   │   ├── CollectionFormats$TSVParams.class
│   │   │   │   │   │   │   │   ├── CollectionFormats.class
│   │   │   │   │   │   │   │   ├── LocalDateAdapter.class
│   │   │   │   │   │   │   │   ├── LocalDateTimeAdapter.class
│   │   │   │   │   │   │   │   ├── OffsetDateTimeAdapter.class
│   │   │   │   │   │   │   │   ├── Serializer.class
│   │   │   │   │   │   │   │   ├── StringBuilderAdapter.class
│   │   │   │   │   │   │   │   ├── URIAdapter.class
│   │   │   │   │   │   │   │   ├── URLAdapter.class
│   │   │   │   │   │   │   │   └── UUIDAdapter.class
│   │   │   │   │   │   │   └── model/
│   │   │   │   │   │   │       ├── AuthenticateAccountRequest$$serializer.class
│   │   │   │   │   │   │       ├── AuthenticateAccountRequest$Companion.class
│   │   │   │   │   │   │       ├── AuthenticateAccountRequest.class
│   │   │   │   │   │   │       ├── AuthTokensResponse$$serializer.class
│   │   │   │   │   │   │       ├── AuthTokensResponse$Companion.class
│   │   │   │   │   │   │       ├── AuthTokensResponse.class
│   │   │   │   │   │   │       ├── CreateSecureItemRequest$$serializer.class
│   │   │   │   │   │   │       ├── CreateSecureItemRequest$Companion.class
│   │   │   │   │   │   │       ├── CreateSecureItemRequest.class
│   │   │   │   │   │   │       ├── CreateSecureItemResult$$serializer.class
│   │   │   │   │   │   │       ├── CreateSecureItemResult$Companion.class
│   │   │   │   │   │   │       ├── CreateSecureItemResult.class
│   │   │   │   │   │   │       ├── CreateUserProfileRequest$$serializer.class
│   │   │   │   │   │   │       ├── CreateUserProfileRequest$Companion.class
│   │   │   │   │   │   │       ├── CreateUserProfileRequest.class
│   │   │   │   │   │   │       ├── DeleteSecureItemResult$$serializer.class
│   │   │   │   │   │   │       ├── DeleteSecureItemResult$Companion.class
│   │   │   │   │   │   │       ├── DeleteSecureItemResult.class
│   │   │   │   │   │   │       ├── Get400Response$$serializer.class
│   │   │   │   │   │   │       ├── Get400Response$Companion.class
│   │   │   │   │   │   │       ├── Get400Response.class
│   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest$$serializer.class
│   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest$Companion.class
│   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest.class
│   │   │   │   │   │   │       ├── ListSecureItemsResponse$$serializer.class
│   │   │   │   │   │   │       ├── ListSecureItemsResponse$Companion.class
│   │   │   │   │   │   │       ├── ListSecureItemsResponse.class
│   │   │   │   │   │   │       ├── RefreshTokenRequest$$serializer.class
│   │   │   │   │   │   │       ├── RefreshTokenRequest$Companion.class
│   │   │   │   │   │   │       ├── RefreshTokenRequest.class
│   │   │   │   │   │   │       ├── RegisterAccountRequest$$serializer.class
│   │   │   │   │   │   │       ├── RegisterAccountRequest$Companion.class
│   │   │   │   │   │   │       ├── RegisterAccountRequest.class
│   │   │   │   │   │   │       ├── RegisterAccountResult$$serializer.class
│   │   │   │   │   │   │       ├── RegisterAccountResult$Companion.class
│   │   │   │   │   │   │       ├── RegisterAccountResult.class
│   │   │   │   │   │   │       ├── SecureItemResponse$$serializer.class
│   │   │   │   │   │   │       ├── SecureItemResponse$Companion.class
│   │   │   │   │   │   │       ├── SecureItemResponse.class
│   │   │   │   │   │   │       ├── SecureItemSummaryResponse$$serializer.class
│   │   │   │   │   │   │       ├── SecureItemSummaryResponse$Companion.class
│   │   │   │   │   │   │       ├── SecureItemSummaryResponse.class
│   │   │   │   │   │   │       ├── UpdateMaster400Response$$serializer.class
│   │   │   │   │   │   │       ├── UpdateMaster400Response$Companion.class
│   │   │   │   │   │   │       ├── UpdateMaster400Response.class
│   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest$$serializer.class
│   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest$Companion.class
│   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest.class
│   │   │   │   │   │   │       ├── UpdateSecureItemRequest$$serializer.class
│   │   │   │   │   │   │       ├── UpdateSecureItemRequest$Companion.class
│   │   │   │   │   │   │       ├── UpdateSecureItemRequest.class
│   │   │   │   │   │   │       ├── UpdateSecureItemResult$$serializer.class
│   │   │   │   │   │   │       ├── UpdateSecureItemResult$Companion.class
│   │   │   │   │   │   │       ├── UpdateSecureItemResult.class
│   │   │   │   │   │   │       ├── UpdateUserProfileRequest$$serializer.class
│   │   │   │   │   │   │       ├── UpdateUserProfileRequest$Companion.class
│   │   │   │   │   │   │       ├── UpdateUserProfileRequest.class
│   │   │   │   │   │   │       ├── UserProfileResponse$$serializer.class
│   │   │   │   │   │   │       ├── UserProfileResponse$Companion.class
│   │   │   │   │   │   │       ├── UserProfileResponse.class
│   │   │   │   │   │   │       ├── VaultKeyMaterialResponse$$serializer.class
│   │   │   │   │   │   │       ├── VaultKeyMaterialResponse$Companion.class
│   │   │   │   │   │   │       └── VaultKeyMaterialResponse.class
│   │   │   │   │   │   ├── AuthInterceptor.class
│   │   │   │   │   │   ├── AuthInterceptor_Factory.class
│   │   │   │   │   │   ├── BuildConfig.class
│   │   │   │   │   │   ├── NetworkClientFactory.class
│   │   │   │   │   │   ├── NetworkClientFactoryTest.class
│   │   │   │   │   │   ├── NetworkConfig.class
│   │   │   │   │   │   ├── PingApi.class
│   │   │   │   │   │   ├── PingResponse$$serializer.class
│   │   │   │   │   │   ├── PingResponse$Companion.class
│   │   │   │   │   │   ├── PingResponse.class
│   │   │   │   │   │   ├── TokenProvider.class
│   │   │   │   │   │   ├── TokenRefreshAuthenticator.class
│   │   │   │   │   │   ├── TokenRefreshAuthenticator_Factory$InstanceHolder.class
│   │   │   │   │   │   └── TokenRefreshAuthenticator_Factory.class
│   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_network_di_NetworkModule.class
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── network.kotlin_module
│   │   │   │   ├── incremental/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── debug-mergeJavaRes/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   └── merge-state
│   │   │   │   │   ├── debugAndroidTest/
│   │   │   │   │   │   ├── mergeDebugAndroidTestResources/
│   │   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   │   │   ├── values/
│   │   │   │   │   │   │   │   │   └── values.xml
│   │   │   │   │   │   │   │   ├── values-af/
│   │   │   │   │   │   │   │   │   └── values-af.xml
│   │   │   │   │   │   │   │   ├── values-am/
│   │   │   │   │   │   │   │   │   └── values-am.xml
│   │   │   │   │   │   │   │   ├── values-ar/
│   │   │   │   │   │   │   │   │   └── values-ar.xml
│   │   │   │   │   │   │   │   ├── values-as/
│   │   │   │   │   │   │   │   │   └── values-as.xml
│   │   │   │   │   │   │   │   ├── values-az/
│   │   │   │   │   │   │   │   │   └── values-az.xml
│   │   │   │   │   │   │   │   ├── values-b+sr+Latn/
│   │   │   │   │   │   │   │   │   └── values-b+sr+Latn.xml
│   │   │   │   │   │   │   │   ├── values-be/
│   │   │   │   │   │   │   │   │   └── values-be.xml
│   │   │   │   │   │   │   │   ├── values-bg/
│   │   │   │   │   │   │   │   │   └── values-bg.xml
│   │   │   │   │   │   │   │   ├── values-bn/
│   │   │   │   │   │   │   │   │   └── values-bn.xml
│   │   │   │   │   │   │   │   ├── values-bs/
│   │   │   │   │   │   │   │   │   └── values-bs.xml
│   │   │   │   │   │   │   │   ├── values-ca/
│   │   │   │   │   │   │   │   │   └── values-ca.xml
│   │   │   │   │   │   │   │   ├── values-cs/
│   │   │   │   │   │   │   │   │   └── values-cs.xml
│   │   │   │   │   │   │   │   ├── values-da/
│   │   │   │   │   │   │   │   │   └── values-da.xml
│   │   │   │   │   │   │   │   ├── values-de/
│   │   │   │   │   │   │   │   │   └── values-de.xml
│   │   │   │   │   │   │   │   ├── values-el/
│   │   │   │   │   │   │   │   │   └── values-el.xml
│   │   │   │   │   │   │   │   ├── values-en-rAU/
│   │   │   │   │   │   │   │   │   └── values-en-rAU.xml
│   │   │   │   │   │   │   │   ├── values-en-rCA/
│   │   │   │   │   │   │   │   │   └── values-en-rCA.xml
│   │   │   │   │   │   │   │   ├── values-en-rGB/
│   │   │   │   │   │   │   │   │   └── values-en-rGB.xml
│   │   │   │   │   │   │   │   ├── values-en-rIN/
│   │   │   │   │   │   │   │   │   └── values-en-rIN.xml
│   │   │   │   │   │   │   │   ├── values-en-rXC/
│   │   │   │   │   │   │   │   │   └── values-en-rXC.xml
│   │   │   │   │   │   │   │   ├── values-es/
│   │   │   │   │   │   │   │   │   └── values-es.xml
│   │   │   │   │   │   │   │   ├── values-es-rUS/
│   │   │   │   │   │   │   │   │   └── values-es-rUS.xml
│   │   │   │   │   │   │   │   ├── values-et/
│   │   │   │   │   │   │   │   │   └── values-et.xml
│   │   │   │   │   │   │   │   ├── values-eu/
│   │   │   │   │   │   │   │   │   └── values-eu.xml
│   │   │   │   │   │   │   │   ├── values-fa/
│   │   │   │   │   │   │   │   │   └── values-fa.xml
│   │   │   │   │   │   │   │   ├── values-fi/
│   │   │   │   │   │   │   │   │   └── values-fi.xml
│   │   │   │   │   │   │   │   ├── values-fr/
│   │   │   │   │   │   │   │   │   └── values-fr.xml
│   │   │   │   │   │   │   │   ├── values-fr-rCA/
│   │   │   │   │   │   │   │   │   └── values-fr-rCA.xml
│   │   │   │   │   │   │   │   ├── values-gl/
│   │   │   │   │   │   │   │   │   └── values-gl.xml
│   │   │   │   │   │   │   │   ├── values-gu/
│   │   │   │   │   │   │   │   │   └── values-gu.xml
│   │   │   │   │   │   │   │   ├── values-hi/
│   │   │   │   │   │   │   │   │   └── values-hi.xml
│   │   │   │   │   │   │   │   ├── values-hr/
│   │   │   │   │   │   │   │   │   └── values-hr.xml
│   │   │   │   │   │   │   │   ├── values-hu/
│   │   │   │   │   │   │   │   │   └── values-hu.xml
│   │   │   │   │   │   │   │   ├── values-hy/
│   │   │   │   │   │   │   │   │   └── values-hy.xml
│   │   │   │   │   │   │   │   ├── values-in/
│   │   │   │   │   │   │   │   │   └── values-in.xml
│   │   │   │   │   │   │   │   ├── values-is/
│   │   │   │   │   │   │   │   │   └── values-is.xml
│   │   │   │   │   │   │   │   ├── values-it/
│   │   │   │   │   │   │   │   │   └── values-it.xml
│   │   │   │   │   │   │   │   ├── values-iw/
│   │   │   │   │   │   │   │   │   └── values-iw.xml
│   │   │   │   │   │   │   │   ├── values-ja/
│   │   │   │   │   │   │   │   │   └── values-ja.xml
│   │   │   │   │   │   │   │   ├── values-ka/
│   │   │   │   │   │   │   │   │   └── values-ka.xml
│   │   │   │   │   │   │   │   ├── values-kk/
│   │   │   │   │   │   │   │   │   └── values-kk.xml
│   │   │   │   │   │   │   │   ├── values-km/
│   │   │   │   │   │   │   │   │   └── values-km.xml
│   │   │   │   │   │   │   │   ├── values-kn/
│   │   │   │   │   │   │   │   │   └── values-kn.xml
│   │   │   │   │   │   │   │   ├── values-ko/
│   │   │   │   │   │   │   │   │   └── values-ko.xml
│   │   │   │   │   │   │   │   ├── values-ky/
│   │   │   │   │   │   │   │   │   └── values-ky.xml
│   │   │   │   │   │   │   │   ├── values-lo/
│   │   │   │   │   │   │   │   │   └── values-lo.xml
│   │   │   │   │   │   │   │   ├── values-lt/
│   │   │   │   │   │   │   │   │   └── values-lt.xml
│   │   │   │   │   │   │   │   ├── values-lv/
│   │   │   │   │   │   │   │   │   └── values-lv.xml
│   │   │   │   │   │   │   │   ├── values-mk/
│   │   │   │   │   │   │   │   │   └── values-mk.xml
│   │   │   │   │   │   │   │   ├── values-ml/
│   │   │   │   │   │   │   │   │   └── values-ml.xml
│   │   │   │   │   │   │   │   ├── values-mn/
│   │   │   │   │   │   │   │   │   └── values-mn.xml
│   │   │   │   │   │   │   │   ├── values-mr/
│   │   │   │   │   │   │   │   │   └── values-mr.xml
│   │   │   │   │   │   │   │   ├── values-ms/
│   │   │   │   │   │   │   │   │   └── values-ms.xml
│   │   │   │   │   │   │   │   ├── values-my/
│   │   │   │   │   │   │   │   │   └── values-my.xml
│   │   │   │   │   │   │   │   ├── values-nb/
│   │   │   │   │   │   │   │   │   └── values-nb.xml
│   │   │   │   │   │   │   │   ├── values-ne/
│   │   │   │   │   │   │   │   │   └── values-ne.xml
│   │   │   │   │   │   │   │   ├── values-nl/
│   │   │   │   │   │   │   │   │   └── values-nl.xml
│   │   │   │   │   │   │   │   ├── values-or/
│   │   │   │   │   │   │   │   │   └── values-or.xml
│   │   │   │   │   │   │   │   ├── values-pa/
│   │   │   │   │   │   │   │   │   └── values-pa.xml
│   │   │   │   │   │   │   │   ├── values-pl/
│   │   │   │   │   │   │   │   │   └── values-pl.xml
│   │   │   │   │   │   │   │   ├── values-pt/
│   │   │   │   │   │   │   │   │   └── values-pt.xml
│   │   │   │   │   │   │   │   ├── values-pt-rBR/
│   │   │   │   │   │   │   │   │   └── values-pt-rBR.xml
│   │   │   │   │   │   │   │   ├── values-pt-rPT/
│   │   │   │   │   │   │   │   │   └── values-pt-rPT.xml
│   │   │   │   │   │   │   │   ├── values-ro/
│   │   │   │   │   │   │   │   │   └── values-ro.xml
│   │   │   │   │   │   │   │   ├── values-ru/
│   │   │   │   │   │   │   │   │   └── values-ru.xml
│   │   │   │   │   │   │   │   ├── values-si/
│   │   │   │   │   │   │   │   │   └── values-si.xml
│   │   │   │   │   │   │   │   ├── values-sk/
│   │   │   │   │   │   │   │   │   └── values-sk.xml
│   │   │   │   │   │   │   │   ├── values-sl/
│   │   │   │   │   │   │   │   │   └── values-sl.xml
│   │   │   │   │   │   │   │   ├── values-sq/
│   │   │   │   │   │   │   │   │   └── values-sq.xml
│   │   │   │   │   │   │   │   ├── values-sr/
│   │   │   │   │   │   │   │   │   └── values-sr.xml
│   │   │   │   │   │   │   │   ├── values-sv/
│   │   │   │   │   │   │   │   │   └── values-sv.xml
│   │   │   │   │   │   │   │   ├── values-sw/
│   │   │   │   │   │   │   │   │   └── values-sw.xml
│   │   │   │   │   │   │   │   ├── values-ta/
│   │   │   │   │   │   │   │   │   └── values-ta.xml
│   │   │   │   │   │   │   │   ├── values-te/
│   │   │   │   │   │   │   │   │   └── values-te.xml
│   │   │   │   │   │   │   │   ├── values-th/
│   │   │   │   │   │   │   │   │   └── values-th.xml
│   │   │   │   │   │   │   │   ├── values-tl/
│   │   │   │   │   │   │   │   │   └── values-tl.xml
│   │   │   │   │   │   │   │   ├── values-tr/
│   │   │   │   │   │   │   │   │   └── values-tr.xml
│   │   │   │   │   │   │   │   ├── values-uk/
│   │   │   │   │   │   │   │   │   └── values-uk.xml
│   │   │   │   │   │   │   │   ├── values-ur/
│   │   │   │   │   │   │   │   │   └── values-ur.xml
│   │   │   │   │   │   │   │   ├── values-uz/
│   │   │   │   │   │   │   │   │   └── values-uz.xml
│   │   │   │   │   │   │   │   ├── values-v21/
│   │   │   │   │   │   │   │   │   └── values-v21.xml
│   │   │   │   │   │   │   │   ├── values-vi/
│   │   │   │   │   │   │   │   │   └── values-vi.xml
│   │   │   │   │   │   │   │   ├── values-zh-rCN/
│   │   │   │   │   │   │   │   │   └── values-zh-rCN.xml
│   │   │   │   │   │   │   │   ├── values-zh-rHK/
│   │   │   │   │   │   │   │   │   └── values-zh-rHK.xml
│   │   │   │   │   │   │   │   ├── values-zh-rTW/
│   │   │   │   │   │   │   │   │   └── values-zh-rTW.xml
│   │   │   │   │   │   │   │   └── values-zu/
│   │   │   │   │   │   │   │       └── values-zu.xml
│   │   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   │   └── packageDebugAndroidTestResources/
│   │   │   │   │   │       ├── merged.dir/
│   │   │   │   │   │       ├── stripped.dir/
│   │   │   │   │   │       │   │   │   │   │   │       └── merger.xml
│   │   │   │   │   ├── debugAndroidTest-mergeJavaRes/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   │   ├── 1ZuCeLYWmYw5MfiXYYnDIw==
│   │   │   │   │   │   │   ├── 3VJ0q7BGqqlcLs7MI_dHlw==
│   │   │   │   │   │   │   ├── 7trlVWptO4XEuV+Pwn+FJg==
│   │   │   │   │   │   │   ├── _hdAXZS0wZVQGsNJYelpuw==
│   │   │   │   │   │   │   ├── Aum8RyvyKLwxGsZvF8wZew==
│   │   │   │   │   │   │   ├── B081BuIDaWSo5McNdeDOlg==
│   │   │   │   │   │   │   ├── BIO8B+IR6k405DzX3KVLfg==
│   │   │   │   │   │   │   ├── eAb6b17AoioR+wuvsUlXUg==
│   │   │   │   │   │   │   ├── EKfNJDNp1qej0g_hpuBvyw==
│   │   │   │   │   │   │   ├── fbJN1jTpNAQTBDjunGpNNw==
│   │   │   │   │   │   │   ├── gMUj5N1rwbVTZNnhBOwS4w==
│   │   │   │   │   │   │   ├── i1fywGgR7739mcd2F7zSGg==
│   │   │   │   │   │   │   ├── IO63ZVBDFar17KQf_eKhoQ==
│   │   │   │   │   │   │   ├── J7CAnpdxVb_3lbUfaCbsMg==
│   │   │   │   │   │   │   ├── J7iYjow924XXI0QA2R4XxA==
│   │   │   │   │   │   │   ├── jrJKw3qGpVbyDMBUg69i1A==
│   │   │   │   │   │   │   ├── kBPjyU0m7mBApb7pIiwI2Q==
│   │   │   │   │   │   │   ├── Kws7Ph2jc1f3d_ecLLw8yA==
│   │   │   │   │   │   │   ├── L+aTIiZM60Y12ESzMr+bBQ==
│   │   │   │   │   │   │   ├── m7ifgdNNq+vMZDPtmarc1Q==
│   │   │   │   │   │   │   ├── MGjkoJZVhVbQnzYuPyFT2g==
│   │   │   │   │   │   │   ├── MoWJlOGWBfjVRC8RvC2PxA==
│   │   │   │   │   │   │   ├── N4JbPtX00mbgf1JBj8hLpA==
│   │   │   │   │   │   │   ├── qD5dgASdHa8TAOro2Bvrtw==
│   │   │   │   │   │   │   ├── qOLoLN95hFQC6sku5Qy+CQ==
│   │   │   │   │   │   │   ├── rrna0K8qZsqmJGvstjZKCQ==
│   │   │   │   │   │   │   ├── tL2pp5i9_KZCC65aeIVhDg==
│   │   │   │   │   │   │   ├── v1FFNCoEO5or6_tvb1ZiNA==
│   │   │   │   │   │   │   └── V8DxNbbYWglX3HsdJ5bXKg==
│   │   │   │   │   │   └── merge-state
│   │   │   │   │   ├── mergeDebugAndroidTestAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugAndroidTestJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeReleaseAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeReleaseJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── packageDebugAndroidTest/tmp/debugAndroidTest/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   │   ├── androidResources
│   │   │   │   │   │   │   └── javaResources0
│   │   │   │   │   │   └── dex-renamer-state.txt
│   │   │   │   │   ├── release/packageReleaseResources/
│   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── transformDebugClassesWithAsm/
│   │   │   │   │   ├── transformDebugUnitTestClassesWithAsm/
│   │   │   │   │   └── transformReleaseClassesWithAsm/
│   │   │   │   ├── java_res/
│   │   │   │   │   ├── debug/processDebugJavaRes/out/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   └── generated/
│   │   │   │   │   │   │       ├── api/
│   │   │   │   │   │   │       ├── auth/
│   │   │   │   │   │   │       ├── infrastructure/
│   │   │   │   │   │   │       └── model/
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── network.kotlin_module
│   │   │   │   │   ├── debugUnitTest/processDebugUnitTestJavaRes/out/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── network.kotlin_module
│   │   │   │   │   └── release/processReleaseJavaRes/out/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/network/di/
│   │   │   │   │       └── META-INF/
│   │   │   │   │           └── network.kotlin_module
│   │   │   │   ├── javac/
│   │   │   │   │   ├── debug/compileDebugJavaWithJavac/classes/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideAuthControllerApiFactory.class
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideJsonFactory$InstanceHolder.class
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideJsonFactory.class
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory$InstanceHolder.class
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory.class
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideOkHttpClientFactory.class
│   │   │   │   │   │   │   │   └── NetworkModule_ProvideRetrofitFactory.class
│   │   │   │   │   │   │   ├── AuthInterceptor_Factory.class
│   │   │   │   │   │   │   ├── BuildConfig.class
│   │   │   │   │   │   │   ├── TokenRefreshAuthenticator_Factory$InstanceHolder.class
│   │   │   │   │   │   │   └── TokenRefreshAuthenticator_Factory.class
│   │   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_core_network_di_NetworkModule.class
│   │   │   │   │   ├── debugAndroidTest/compileDebugAndroidTestJavaWithJavac/classes/com/miguelrodriguez19/safecube/core/network/test/
│   │   │   │   │   │   └── BuildConfig.class
│   │   │   │   │   └── release/compileReleaseJavaWithJavac/classes/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │       │   ├── di/
│   │   │   │   │       │   │   ├── NetworkModule_ProvideApiServiceFactory.class
│   │   │   │   │       │   │   ├── NetworkModule_ProvideJsonFactory$InstanceHolder.class
│   │   │   │   │       │   │   ├── NetworkModule_ProvideJsonFactory.class
│   │   │   │   │       │   │   ├── NetworkModule_ProvideNetworkConfigFactory$InstanceHolder.class
│   │   │   │   │       │   │   ├── NetworkModule_ProvideNetworkConfigFactory.class
│   │   │   │   │       │   │   ├── NetworkModule_ProvideOkHttpClientFactory.class
│   │   │   │   │       │   │   └── NetworkModule_ProvideRetrofitFactory.class
│   │   │   │   │       │   ├── AuthInterceptor_Factory.class
│   │   │   │   │       │   └── BuildConfig.class
│   │   │   │   │       └── hilt_aggregated_deps/
│   │   │   │   │           └── _com_miguelrodriguez19_safecube_core_network_di_NetworkModule.class
│   │   │   │   ├── library_and_local_jars_jni/debug/copyDebugJniLibsProjectAndLocalJars/jni/
│   │   │   │   ├── library_art_profile/
│   │   │   │   │   ├── debug/prepareDebugArtProfile/
│   │   │   │   │   └── release/prepareReleaseArtProfile/
│   │   │   │   ├── library_jni/
│   │   │   │   │   ├── debug/copyDebugJniLibsProjectOnly/jni/
│   │   │   │   │   └── release/copyReleaseJniLibsProjectOnly/jni/
│   │   │   │   ├── linked_resources_binary_format/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   ├── linked-resources-binary-format.ap_
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── lint_publish_jar/global/prepareLintJarForPublish/
│   │   │   │   ├── local_only_symbol_list/
│   │   │   │   │   ├── debug/parseDebugLocalResources/
│   │   │   │   │   │   └── R-def.txt
│   │   │   │   │   ├── debugAndroidTest/parseDebugAndroidTestLocalResources/
│   │   │   │   │   │   └── R-def.txt
│   │   │   │   │   └── release/parseReleaseLocalResources/
│   │   │   │   │       └── R-def.txt
│   │   │   │   ├── manifest_merge_blame_file/
│   │   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   │   └── manifest-merger-blame-debug-report.txt
│   │   │   │   │   ├── debugAndroidTest/processDebugAndroidTestManifest/
│   │   │   │   │   │   └── manifest-merger-blame-debug-androidTest-report.txt
│   │   │   │   │   └── release/processReleaseManifest/
│   │   │   │   │       └── manifest-merger-blame-release-report.txt
│   │   │   │   ├── merged_consumer_proguard_file/debug/mergeDebugConsumerProguardFiles/
│   │   │   │   ├── merged_java_res/
│   │   │   │   │   ├── debug/mergeDebugJavaResource/
│   │   │   │   │   │   └── feature-network.jar
│   │   │   │   │   └── debugAndroidTest/mergeDebugAndroidTestJavaResource/
│   │   │   │   │       └── feature-network.jar
│   │   │   │   ├── merged_jni_libs/
│   │   │   │   │   ├── debug/mergeDebugJniLibFolders/out/
│   │   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestJniLibFolders/out/
│   │   │   │   │   └── release/mergeReleaseJniLibFolders/out/
│   │   │   │   ├── merged_manifest/
│   │   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   │   └── AndroidManifest.xml
│   │   │   │   │   └── release/processReleaseManifest/
│   │   │   │   │       └── AndroidManifest.xml
│   │   │   │   ├── merged_res/debugAndroidTest/mergeDebugAndroidTestResources/
│   │   │   │   │   ├── values-af_values-af.arsc.flat
│   │   │   │   │   ├── values-am_values-am.arsc.flat
│   │   │   │   │   ├── values-ar_values-ar.arsc.flat
│   │   │   │   │   ├── values-as_values-as.arsc.flat
│   │   │   │   │   ├── values-az_values-az.arsc.flat
│   │   │   │   │   ├── values-b+sr+Latn_values-b+sr+Latn.arsc.flat
│   │   │   │   │   ├── values-be_values-be.arsc.flat
│   │   │   │   │   ├── values-bg_values-bg.arsc.flat
│   │   │   │   │   ├── values-bn_values-bn.arsc.flat
│   │   │   │   │   ├── values-bs_values-bs.arsc.flat
│   │   │   │   │   ├── values-ca_values-ca.arsc.flat
│   │   │   │   │   ├── values-cs_values-cs.arsc.flat
│   │   │   │   │   ├── values-da_values-da.arsc.flat
│   │   │   │   │   ├── values-de_values-de.arsc.flat
│   │   │   │   │   ├── values-el_values-el.arsc.flat
│   │   │   │   │   ├── values-en-rAU_values-en-rAU.arsc.flat
│   │   │   │   │   ├── values-en-rCA_values-en-rCA.arsc.flat
│   │   │   │   │   ├── values-en-rGB_values-en-rGB.arsc.flat
│   │   │   │   │   ├── values-en-rIN_values-en-rIN.arsc.flat
│   │   │   │   │   ├── values-en-rXC_values-en-rXC.arsc.flat
│   │   │   │   │   ├── values-es-rUS_values-es-rUS.arsc.flat
│   │   │   │   │   ├── values-es_values-es.arsc.flat
│   │   │   │   │   ├── values-et_values-et.arsc.flat
│   │   │   │   │   ├── values-eu_values-eu.arsc.flat
│   │   │   │   │   ├── values-fa_values-fa.arsc.flat
│   │   │   │   │   ├── values-fi_values-fi.arsc.flat
│   │   │   │   │   ├── values-fr-rCA_values-fr-rCA.arsc.flat
│   │   │   │   │   ├── values-fr_values-fr.arsc.flat
│   │   │   │   │   ├── values-gl_values-gl.arsc.flat
│   │   │   │   │   ├── values-gu_values-gu.arsc.flat
│   │   │   │   │   ├── values-hi_values-hi.arsc.flat
│   │   │   │   │   ├── values-hr_values-hr.arsc.flat
│   │   │   │   │   ├── values-hu_values-hu.arsc.flat
│   │   │   │   │   ├── values-hy_values-hy.arsc.flat
│   │   │   │   │   ├── values-in_values-in.arsc.flat
│   │   │   │   │   ├── values-is_values-is.arsc.flat
│   │   │   │   │   ├── values-it_values-it.arsc.flat
│   │   │   │   │   ├── values-iw_values-iw.arsc.flat
│   │   │   │   │   ├── values-ja_values-ja.arsc.flat
│   │   │   │   │   ├── values-ka_values-ka.arsc.flat
│   │   │   │   │   ├── values-kk_values-kk.arsc.flat
│   │   │   │   │   ├── values-km_values-km.arsc.flat
│   │   │   │   │   ├── values-kn_values-kn.arsc.flat
│   │   │   │   │   ├── values-ko_values-ko.arsc.flat
│   │   │   │   │   ├── values-ky_values-ky.arsc.flat
│   │   │   │   │   ├── values-lo_values-lo.arsc.flat
│   │   │   │   │   ├── values-lt_values-lt.arsc.flat
│   │   │   │   │   ├── values-lv_values-lv.arsc.flat
│   │   │   │   │   ├── values-mk_values-mk.arsc.flat
│   │   │   │   │   ├── values-ml_values-ml.arsc.flat
│   │   │   │   │   ├── values-mn_values-mn.arsc.flat
│   │   │   │   │   ├── values-mr_values-mr.arsc.flat
│   │   │   │   │   ├── values-ms_values-ms.arsc.flat
│   │   │   │   │   ├── values-my_values-my.arsc.flat
│   │   │   │   │   ├── values-nb_values-nb.arsc.flat
│   │   │   │   │   ├── values-ne_values-ne.arsc.flat
│   │   │   │   │   ├── values-nl_values-nl.arsc.flat
│   │   │   │   │   ├── values-or_values-or.arsc.flat
│   │   │   │   │   ├── values-pa_values-pa.arsc.flat
│   │   │   │   │   ├── values-pl_values-pl.arsc.flat
│   │   │   │   │   ├── values-pt-rBR_values-pt-rBR.arsc.flat
│   │   │   │   │   ├── values-pt-rPT_values-pt-rPT.arsc.flat
│   │   │   │   │   ├── values-pt_values-pt.arsc.flat
│   │   │   │   │   ├── values-ro_values-ro.arsc.flat
│   │   │   │   │   ├── values-ru_values-ru.arsc.flat
│   │   │   │   │   ├── values-si_values-si.arsc.flat
│   │   │   │   │   ├── values-sk_values-sk.arsc.flat
│   │   │   │   │   ├── values-sl_values-sl.arsc.flat
│   │   │   │   │   ├── values-sq_values-sq.arsc.flat
│   │   │   │   │   ├── values-sr_values-sr.arsc.flat
│   │   │   │   │   ├── values-sv_values-sv.arsc.flat
│   │   │   │   │   ├── values-sw_values-sw.arsc.flat
│   │   │   │   │   ├── values-ta_values-ta.arsc.flat
│   │   │   │   │   ├── values-te_values-te.arsc.flat
│   │   │   │   │   ├── values-th_values-th.arsc.flat
│   │   │   │   │   ├── values-tl_values-tl.arsc.flat
│   │   │   │   │   ├── values-tr_values-tr.arsc.flat
│   │   │   │   │   ├── values-uk_values-uk.arsc.flat
│   │   │   │   │   ├── values-ur_values-ur.arsc.flat
│   │   │   │   │   ├── values-uz_values-uz.arsc.flat
│   │   │   │   │   ├── values-v21_values-v21.arsc.flat
│   │   │   │   │   ├── values-vi_values-vi.arsc.flat
│   │   │   │   │   ├── values-zh-rCN_values-zh-rCN.arsc.flat
│   │   │   │   │   ├── values-zh-rHK_values-zh-rHK.arsc.flat
│   │   │   │   │   ├── values-zh-rTW_values-zh-rTW.arsc.flat
│   │   │   │   │   ├── values-zu_values-zu.arsc.flat
│   │   │   │   │   └── values_values.arsc.flat
│   │   │   │   ├── merged_res_blame_folder/debugAndroidTest/mergeDebugAndroidTestResources/out/multi-v2/
│   │   │   │   │   ├── mergeDebugAndroidTestResources.json
│   │   │   │   │   ├── values-af.json
│   │   │   │   │   ├── values-am.json
│   │   │   │   │   ├── values-ar.json
│   │   │   │   │   ├── values-as.json
│   │   │   │   │   ├── values-az.json
│   │   │   │   │   ├── values-b+sr+Latn.json
│   │   │   │   │   ├── values-be.json
│   │   │   │   │   ├── values-bg.json
│   │   │   │   │   ├── values-bn.json
│   │   │   │   │   ├── values-bs.json
│   │   │   │   │   ├── values-ca.json
│   │   │   │   │   ├── values-cs.json
│   │   │   │   │   ├── values-da.json
│   │   │   │   │   ├── values-de.json
│   │   │   │   │   ├── values-el.json
│   │   │   │   │   ├── values-en-rAU.json
│   │   │   │   │   ├── values-en-rCA.json
│   │   │   │   │   ├── values-en-rGB.json
│   │   │   │   │   ├── values-en-rIN.json
│   │   │   │   │   ├── values-en-rXC.json
│   │   │   │   │   ├── values-es-rUS.json
│   │   │   │   │   ├── values-es.json
│   │   │   │   │   ├── values-et.json
│   │   │   │   │   ├── values-eu.json
│   │   │   │   │   ├── values-fa.json
│   │   │   │   │   ├── values-fi.json
│   │   │   │   │   ├── values-fr-rCA.json
│   │   │   │   │   ├── values-fr.json
│   │   │   │   │   ├── values-gl.json
│   │   │   │   │   ├── values-gu.json
│   │   │   │   │   ├── values-hi.json
│   │   │   │   │   ├── values-hr.json
│   │   │   │   │   ├── values-hu.json
│   │   │   │   │   ├── values-hy.json
│   │   │   │   │   ├── values-in.json
│   │   │   │   │   ├── values-is.json
│   │   │   │   │   ├── values-it.json
│   │   │   │   │   ├── values-iw.json
│   │   │   │   │   ├── values-ja.json
│   │   │   │   │   ├── values-ka.json
│   │   │   │   │   ├── values-kk.json
│   │   │   │   │   ├── values-km.json
│   │   │   │   │   ├── values-kn.json
│   │   │   │   │   ├── values-ko.json
│   │   │   │   │   ├── values-ky.json
│   │   │   │   │   ├── values-lo.json
│   │   │   │   │   ├── values-lt.json
│   │   │   │   │   ├── values-lv.json
│   │   │   │   │   ├── values-mk.json
│   │   │   │   │   ├── values-ml.json
│   │   │   │   │   ├── values-mn.json
│   │   │   │   │   ├── values-mr.json
│   │   │   │   │   ├── values-ms.json
│   │   │   │   │   ├── values-my.json
│   │   │   │   │   ├── values-nb.json
│   │   │   │   │   ├── values-ne.json
│   │   │   │   │   ├── values-nl.json
│   │   │   │   │   ├── values-or.json
│   │   │   │   │   ├── values-pa.json
│   │   │   │   │   ├── values-pl.json
│   │   │   │   │   ├── values-pt-rBR.json
│   │   │   │   │   ├── values-pt-rPT.json
│   │   │   │   │   ├── values-pt.json
│   │   │   │   │   ├── values-ro.json
│   │   │   │   │   ├── values-ru.json
│   │   │   │   │   ├── values-si.json
│   │   │   │   │   ├── values-sk.json
│   │   │   │   │   ├── values-sl.json
│   │   │   │   │   ├── values-sq.json
│   │   │   │   │   ├── values-sr.json
│   │   │   │   │   ├── values-sv.json
│   │   │   │   │   ├── values-sw.json
│   │   │   │   │   ├── values-ta.json
│   │   │   │   │   ├── values-te.json
│   │   │   │   │   ├── values-th.json
│   │   │   │   │   ├── values-tl.json
│   │   │   │   │   ├── values-tr.json
│   │   │   │   │   ├── values-uk.json
│   │   │   │   │   ├── values-ur.json
│   │   │   │   │   ├── values-uz.json
│   │   │   │   │   ├── values-v21.json
│   │   │   │   │   ├── values-vi.json
│   │   │   │   │   ├── values-zh-rCN.json
│   │   │   │   │   ├── values-zh-rHK.json
│   │   │   │   │   ├── values-zh-rTW.json
│   │   │   │   │   ├── values-zu.json
│   │   │   │   │   └── values.json
│   │   │   │   ├── mixed_scope_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── navigation_json/
│   │   │   │   │   ├── debug/extractDeepLinksDebug/
│   │   │   │   │   │   └── navigation.json
│   │   │   │   │   └── release/extractDeepLinksRelease/
│   │   │   │   │       └── navigation.json
│   │   │   │   ├── navigation_json_for_aar/debug/extractDeepLinksForAarDebug/
│   │   │   │   ├── nested_resources_validation_report/
│   │   │   │   │   ├── debug/generateDebugResources/
│   │   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestResources/
│   │   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   │   └── release/generateReleaseResources/
│   │   │   │   │       └── nestedResourcesValidationReport.txt
│   │   │   │   ├── packaged_manifests/debugAndroidTest/processDebugAndroidTestManifest/
│   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── packaged_res/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   ├── project_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/test/
│   │   │   │   │   │   └── BuildConfig.dex
│   │   │   │   │   ├── f98b92693975ecb5381308606c9d938bc4e3223880f83ba1b6fab6c5c1551980_0.jar
│   │   │   │   │   ├── f98b92693975ecb5381308606c9d938bc4e3223880f83ba1b6fab6c5c1551980_1.jar
│   │   │   │   │   ├── f98b92693975ecb5381308606c9d938bc4e3223880f83ba1b6fab6c5c1551980_2.jar
│   │   │   │   │   ├── f98b92693975ecb5381308606c9d938bc4e3223880f83ba1b6fab6c5c1551980_3.jar
│   │   │   │   │   ├── f98b92693975ecb5381308606c9d938bc4e3223880f83ba1b6fab6c5c1551980_4.jar
│   │   │   │   │   └── f98b92693975ecb5381308606c9d938bc4e3223880f83ba1b6fab6c5c1551980_5.jar
│   │   │   │   ├── public_res/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   ├── runtime_library_classes_dir/debug/bundleLibRuntimeToDirDebug/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   ├── NetworkModule.class
│   │   │   │   │   │   │   ├── NetworkModule_ProvideAuthControllerApiFactory.class
│   │   │   │   │   │   │   ├── NetworkModule_ProvideJsonFactory$InstanceHolder.class
│   │   │   │   │   │   │   ├── NetworkModule_ProvideJsonFactory.class
│   │   │   │   │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory$InstanceHolder.class
│   │   │   │   │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory.class
│   │   │   │   │   │   │   ├── NetworkModule_ProvideOkHttpClientFactory.class
│   │   │   │   │   │   │   └── NetworkModule_ProvideRetrofitFactory.class
│   │   │   │   │   │   ├── generated/
│   │   │   │   │   │   │   ├── api/
│   │   │   │   │   │   │   │   ├── AuthControllerApi.class
│   │   │   │   │   │   │   │   ├── UserProfileControllerApi.class
│   │   │   │   │   │   │   │   ├── VaultControllerApi$DefaultImpls.class
│   │   │   │   │   │   │   │   ├── VaultControllerApi.class
│   │   │   │   │   │   │   │   └── VaultKeyMaterialControllerApi.class
│   │   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   │   │   └── HttpBearerAuth.class
│   │   │   │   │   │   │   ├── infrastructure/
│   │   │   │   │   │   │   │   ├── ApiClient$Companion.class
│   │   │   │   │   │   │   │   ├── ApiClient.class
│   │   │   │   │   │   │   │   ├── AtomicBooleanAdapter.class
│   │   │   │   │   │   │   │   ├── AtomicIntegerAdapter.class
│   │   │   │   │   │   │   │   ├── AtomicLongAdapter.class
│   │   │   │   │   │   │   │   ├── BigDecimalAdapter.class
│   │   │   │   │   │   │   │   ├── BigIntegerAdapter.class
│   │   │   │   │   │   │   │   ├── CollectionFormats$CSVParams.class
│   │   │   │   │   │   │   │   ├── CollectionFormats$PIPESParams.class
│   │   │   │   │   │   │   │   ├── CollectionFormats$SPACEParams.class
│   │   │   │   │   │   │   │   ├── CollectionFormats$SSVParams.class
│   │   │   │   │   │   │   │   ├── CollectionFormats$TSVParams.class
│   │   │   │   │   │   │   │   ├── CollectionFormats.class
│   │   │   │   │   │   │   │   ├── LocalDateAdapter.class
│   │   │   │   │   │   │   │   ├── LocalDateTimeAdapter.class
│   │   │   │   │   │   │   │   ├── OffsetDateTimeAdapter.class
│   │   │   │   │   │   │   │   ├── Serializer.class
│   │   │   │   │   │   │   │   ├── StringBuilderAdapter.class
│   │   │   │   │   │   │   │   ├── URIAdapter.class
│   │   │   │   │   │   │   │   ├── URLAdapter.class
│   │   │   │   │   │   │   │   └── UUIDAdapter.class
│   │   │   │   │   │   │   └── model/
│   │   │   │   │   │   │       ├── AuthenticateAccountRequest$$serializer.class
│   │   │   │   │   │   │       ├── AuthenticateAccountRequest$Companion.class
│   │   │   │   │   │   │       ├── AuthenticateAccountRequest.class
│   │   │   │   │   │   │       ├── AuthTokensResponse$$serializer.class
│   │   │   │   │   │   │       ├── AuthTokensResponse$Companion.class
│   │   │   │   │   │   │       ├── AuthTokensResponse.class
│   │   │   │   │   │   │       ├── CreateSecureItemRequest$$serializer.class
│   │   │   │   │   │   │       ├── CreateSecureItemRequest$Companion.class
│   │   │   │   │   │   │       ├── CreateSecureItemRequest.class
│   │   │   │   │   │   │       ├── CreateSecureItemResult$$serializer.class
│   │   │   │   │   │   │       ├── CreateSecureItemResult$Companion.class
│   │   │   │   │   │   │       ├── CreateSecureItemResult.class
│   │   │   │   │   │   │       ├── CreateUserProfileRequest$$serializer.class
│   │   │   │   │   │   │       ├── CreateUserProfileRequest$Companion.class
│   │   │   │   │   │   │       ├── CreateUserProfileRequest.class
│   │   │   │   │   │   │       ├── DeleteSecureItemResult$$serializer.class
│   │   │   │   │   │   │       ├── DeleteSecureItemResult$Companion.class
│   │   │   │   │   │   │       ├── DeleteSecureItemResult.class
│   │   │   │   │   │   │       ├── Get400Response$$serializer.class
│   │   │   │   │   │   │       ├── Get400Response$Companion.class
│   │   │   │   │   │   │       ├── Get400Response.class
│   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest$$serializer.class
│   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest$Companion.class
│   │   │   │   │   │   │       ├── InitVaultKeyMaterialRequest.class
│   │   │   │   │   │   │       ├── ListSecureItemsResponse$$serializer.class
│   │   │   │   │   │   │       ├── ListSecureItemsResponse$Companion.class
│   │   │   │   │   │   │       ├── ListSecureItemsResponse.class
│   │   │   │   │   │   │       ├── RefreshTokenRequest$$serializer.class
│   │   │   │   │   │   │       ├── RefreshTokenRequest$Companion.class
│   │   │   │   │   │   │       ├── RefreshTokenRequest.class
│   │   │   │   │   │   │       ├── RegisterAccountRequest$$serializer.class
│   │   │   │   │   │   │       ├── RegisterAccountRequest$Companion.class
│   │   │   │   │   │   │       ├── RegisterAccountRequest.class
│   │   │   │   │   │   │       ├── RegisterAccountResult$$serializer.class
│   │   │   │   │   │   │       ├── RegisterAccountResult$Companion.class
│   │   │   │   │   │   │       ├── RegisterAccountResult.class
│   │   │   │   │   │   │       ├── SecureItemResponse$$serializer.class
│   │   │   │   │   │   │       ├── SecureItemResponse$Companion.class
│   │   │   │   │   │   │       ├── SecureItemResponse.class
│   │   │   │   │   │   │       ├── SecureItemSummaryResponse$$serializer.class
│   │   │   │   │   │   │       ├── SecureItemSummaryResponse$Companion.class
│   │   │   │   │   │   │       ├── SecureItemSummaryResponse.class
│   │   │   │   │   │   │       ├── UpdateMaster400Response$$serializer.class
│   │   │   │   │   │   │       ├── UpdateMaster400Response$Companion.class
│   │   │   │   │   │   │       ├── UpdateMaster400Response.class
│   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest$$serializer.class
│   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest$Companion.class
│   │   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest.class
│   │   │   │   │   │   │       ├── UpdateSecureItemRequest$$serializer.class
│   │   │   │   │   │   │       ├── UpdateSecureItemRequest$Companion.class
│   │   │   │   │   │   │       ├── UpdateSecureItemRequest.class
│   │   │   │   │   │   │       ├── UpdateSecureItemResult$$serializer.class
│   │   │   │   │   │   │       ├── UpdateSecureItemResult$Companion.class
│   │   │   │   │   │   │       ├── UpdateSecureItemResult.class
│   │   │   │   │   │   │       ├── UpdateUserProfileRequest$$serializer.class
│   │   │   │   │   │   │       ├── UpdateUserProfileRequest$Companion.class
│   │   │   │   │   │   │       ├── UpdateUserProfileRequest.class
│   │   │   │   │   │   │       ├── UserProfileResponse$$serializer.class
│   │   │   │   │   │   │       ├── UserProfileResponse$Companion.class
│   │   │   │   │   │   │       ├── UserProfileResponse.class
│   │   │   │   │   │   │       ├── VaultKeyMaterialResponse$$serializer.class
│   │   │   │   │   │   │       ├── VaultKeyMaterialResponse$Companion.class
│   │   │   │   │   │   │       └── VaultKeyMaterialResponse.class
│   │   │   │   │   │   ├── AuthInterceptor.class
│   │   │   │   │   │   ├── AuthInterceptor_Factory.class
│   │   │   │   │   │   ├── BuildConfig.class
│   │   │   │   │   │   ├── NetworkClientFactory.class
│   │   │   │   │   │   ├── NetworkConfig.class
│   │   │   │   │   │   ├── TokenProvider.class
│   │   │   │   │   │   ├── TokenRefreshAuthenticator.class
│   │   │   │   │   │   ├── TokenRefreshAuthenticator_Factory$InstanceHolder.class
│   │   │   │   │   │   └── TokenRefreshAuthenticator_Factory.class
│   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_network_di_NetworkModule.class
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── network.kotlin_module
│   │   │   │   ├── runtime_library_classes_jar/
│   │   │   │   │   ├── debug/bundleLibRuntimeToJarDebug/
│   │   │   │   │   │   └── classes.jar
│   │   │   │   │   └── release/bundleLibRuntimeToJarRelease/
│   │   │   │   │       └── classes.jar
│   │   │   │   ├── runtime_symbol_list/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── R.txt
│   │   │   │   ├── signing_config_versions/debugAndroidTest/writeDebugAndroidTestSigningConfigVersions/
│   │   │   │   │   └── signing-config-versions.json
│   │   │   │   ├── stable_resource_ids_file/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── stableIds.txt
│   │   │   │   ├── sub_project_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── symbol_list_with_package_name/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── package-aware-r.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── package-aware-r.txt
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── package-aware-r.txt
│   │   │   │   ├── tmp/manifest/androidTest/debug/
│   │   │   │   └── validate_signing_config/debugAndroidTest/validateSigningDebugAndroidTest/
│   │   │   ├── kotlin/
│   │   │   │   ├── compileDebugKotlin/
│   │   │   │   │   ├── cacheable/
│   │   │   │   │   │   ├── caches-jvm/
│   │   │   │   │   │   │   ├── compilerPluginFiles/
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │   │   │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │   │   │   │   ├── inputs/
│   │   │   │   │   │   │   │   ├── source-to-output.tab
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.values
│   │   │   │   │   │   │   │   ├── source-to-output.tab.values.at
│   │   │   │   │   │   │   │   ├── source-to-output.tab.values.s
│   │   │   │   │   │   │   │   ├── source-to-output.tab_i
│   │   │   │   │   │   │   │   └── source-to-output.tab_i.len
│   │   │   │   │   │   │   ├── jvm/
│   │   │   │   │   │   │   │   └── kotlin/
│   │   │   │   │   │   │   │       ├── class-attributes.tab
│   │   │   │   │   │   │   │       ├── class-attributes.tab.keystream
│   │   │   │   │   │   │   │       ├── class-attributes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── class-attributes.tab.len
│   │   │   │   │   │   │   │       ├── class-attributes.tab.values.at
│   │   │   │   │   │   │   │       ├── class-attributes.tab_i
│   │   │   │   │   │   │   │       ├── class-attributes.tab_i.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.values.at
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i.len
│   │   │   │   │   │   │   │       ├── inline-functions.tab
│   │   │   │   │   │   │   │       ├── inline-functions.tab.keystream
│   │   │   │   │   │   │   │       ├── inline-functions.tab.keystream.len
│   │   │   │   │   │   │   │       ├── inline-functions.tab.len
│   │   │   │   │   │   │   │       ├── inline-functions.tab.values.at
│   │   │   │   │   │   │   │       ├── inline-functions.tab_i
│   │   │   │   │   │   │   │       ├── inline-functions.tab_i.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │   │   │   │   │       ├── proto.tab
│   │   │   │   │   │   │   │       ├── proto.tab.keystream
│   │   │   │   │   │   │   │       ├── proto.tab.keystream.len
│   │   │   │   │   │   │   │       ├── proto.tab.len
│   │   │   │   │   │   │   │       ├── proto.tab.values
│   │   │   │   │   │   │   │       ├── proto.tab.values.at
│   │   │   │   │   │   │   │       ├── proto.tab.values.s
│   │   │   │   │   │   │   │       ├── proto.tab_i
│   │   │   │   │   │   │   │       ├── proto.tab_i.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │   │   │   │   │       ├── source-to-classes.tab_i
│   │   │   │   │   │   │   │       ├── source-to-classes.tab_i.len
│   │   │   │   │   │   │   │       ├── subtypes.tab
│   │   │   │   │   │   │   │       ├── subtypes.tab.keystream
│   │   │   │   │   │   │   │       ├── subtypes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── subtypes.tab.len
│   │   │   │   │   │   │   │       ├── subtypes.tab.values.at
│   │   │   │   │   │   │   │       ├── subtypes.tab_i
│   │   │   │   │   │   │   │       ├── subtypes.tab_i.len
│   │   │   │   │   │   │   │       ├── supertypes.tab
│   │   │   │   │   │   │   │       ├── supertypes.tab.keystream
│   │   │   │   │   │   │   │       ├── supertypes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── supertypes.tab.len
│   │   │   │   │   │   │   │       ├── supertypes.tab.values.at
│   │   │   │   │   │   │   │       ├── supertypes.tab_i
│   │   │   │   │   │   │   │       └── supertypes.tab_i.len
│   │   │   │   │   │   │   └── lookups/
│   │   │   │   │   │   │       ├── counters.tab
│   │   │   │   │   │   │       ├── file-to-id.tab
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream.len
│   │   │   │   │   │   │       ├── file-to-id.tab.len
│   │   │   │   │   │   │       ├── file-to-id.tab.values.at
│   │   │   │   │   │   │       ├── file-to-id.tab_i
│   │   │   │   │   │   │       ├── file-to-id.tab_i.len
│   │   │   │   │   │   │       ├── id-to-file.tab
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream.len
│   │   │   │   │   │   │       ├── id-to-file.tab.len
│   │   │   │   │   │   │       ├── id-to-file.tab.values.at
│   │   │   │   │   │   │       ├── id-to-file.tab_i
│   │   │   │   │   │   │       ├── id-to-file.tab_i.len
│   │   │   │   │   │   │       ├── lookups.tab
│   │   │   │   │   │   │       ├── lookups.tab.keystream
│   │   │   │   │   │   │       ├── lookups.tab.keystream.len
│   │   │   │   │   │   │       ├── lookups.tab.len
│   │   │   │   │   │   │       ├── lookups.tab.values.at
│   │   │   │   │   │   │       ├── lookups.tab_i
│   │   │   │   │   │   │       └── lookups.tab_i.len
│   │   │   │   │   │   └── last-build.bin
│   │   │   │   │   ├── classpath-snapshot/
│   │   │   │   │   │   └── shrunk-classpath-snapshot.bin
│   │   │   │   │   └── local-state/
│   │   │   │   ├── compileDebugUnitTestKotlin/
│   │   │   │   │   ├── cacheable/
│   │   │   │   │   │   ├── caches-jvm/
│   │   │   │   │   │   │   ├── compilerPluginFiles/
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │   │   │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │   │   │   │   ├── inputs/
│   │   │   │   │   │   │   │   ├── source-to-output.tab
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.values.at
│   │   │   │   │   │   │   │   ├── source-to-output.tab_i
│   │   │   │   │   │   │   │   └── source-to-output.tab_i.len
│   │   │   │   │   │   │   ├── jvm/
│   │   │   │   │   │   │   │   └── kotlin/
│   │   │   │   │   │   │   │       ├── class-attributes.tab
│   │   │   │   │   │   │   │       ├── class-attributes.tab.keystream
│   │   │   │   │   │   │   │       ├── class-attributes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── class-attributes.tab.len
│   │   │   │   │   │   │   │       ├── class-attributes.tab.values.at
│   │   │   │   │   │   │   │       ├── class-attributes.tab_i
│   │   │   │   │   │   │   │       ├── class-attributes.tab_i.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.values.at
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │   │   │   │   │       ├── proto.tab
│   │   │   │   │   │   │   │       ├── proto.tab.keystream
│   │   │   │   │   │   │   │       ├── proto.tab.keystream.len
│   │   │   │   │   │   │   │       ├── proto.tab.len
│   │   │   │   │   │   │   │       ├── proto.tab.values.at
│   │   │   │   │   │   │   │       ├── proto.tab_i
│   │   │   │   │   │   │   │       ├── proto.tab_i.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │   │   │   │   │       ├── source-to-classes.tab_i
│   │   │   │   │   │   │   │       ├── source-to-classes.tab_i.len
│   │   │   │   │   │   │   │       ├── subtypes.tab
│   │   │   │   │   │   │   │       ├── subtypes.tab.keystream
│   │   │   │   │   │   │   │       ├── subtypes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── subtypes.tab.len
│   │   │   │   │   │   │   │       ├── subtypes.tab.values.at
│   │   │   │   │   │   │   │       ├── subtypes.tab_i
│   │   │   │   │   │   │   │       ├── subtypes.tab_i.len
│   │   │   │   │   │   │   │       ├── supertypes.tab
│   │   │   │   │   │   │   │       ├── supertypes.tab.keystream
│   │   │   │   │   │   │   │       ├── supertypes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── supertypes.tab.len
│   │   │   │   │   │   │   │       ├── supertypes.tab.values.at
│   │   │   │   │   │   │   │       ├── supertypes.tab_i
│   │   │   │   │   │   │   │       └── supertypes.tab_i.len
│   │   │   │   │   │   │   └── lookups/
│   │   │   │   │   │   │       ├── counters.tab
│   │   │   │   │   │   │       ├── file-to-id.tab
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream.len
│   │   │   │   │   │   │       ├── file-to-id.tab.len
│   │   │   │   │   │   │       ├── file-to-id.tab.values.at
│   │   │   │   │   │   │       ├── file-to-id.tab_i
│   │   │   │   │   │   │       ├── file-to-id.tab_i.len
│   │   │   │   │   │   │       ├── id-to-file.tab
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream.len
│   │   │   │   │   │   │       ├── id-to-file.tab.len
│   │   │   │   │   │   │       ├── id-to-file.tab.values.at
│   │   │   │   │   │   │       ├── id-to-file.tab_i.len
│   │   │   │   │   │   │       ├── lookups.tab
│   │   │   │   │   │   │       ├── lookups.tab.keystream
│   │   │   │   │   │   │       ├── lookups.tab.keystream.len
│   │   │   │   │   │   │       ├── lookups.tab.len
│   │   │   │   │   │   │       ├── lookups.tab.values.at
│   │   │   │   │   │   │       ├── lookups.tab_i
│   │   │   │   │   │   │       └── lookups.tab_i.len
│   │   │   │   │   │   └── last-build.bin
│   │   │   │   │   ├── classpath-snapshot/
│   │   │   │   │   │   └── shrunk-classpath-snapshot.bin
│   │   │   │   │   └── local-state/
│   │   │   │   └── compileReleaseKotlin/
│   │   │   │       ├── cacheable/
│   │   │   │       │   ├── caches-jvm/
│   │   │   │       │   │   ├── compilerPluginFiles/
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │       │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │       │   │   ├── inputs/
│   │   │   │       │   │   │   ├── source-to-output.tab
│   │   │   │       │   │   │   ├── source-to-output.tab.keystream
│   │   │   │       │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │       │   │   │   ├── source-to-output.tab.len
│   │   │   │       │   │   │   ├── source-to-output.tab.values.at
│   │   │   │       │   │   │   ├── source-to-output.tab_i
│   │   │   │       │   │   │   └── source-to-output.tab_i.len
│   │   │   │       │   │   ├── jvm/
│   │   │   │       │   │   │   └── kotlin/
│   │   │   │       │   │   │       ├── class-attributes.tab
│   │   │   │       │   │   │       ├── class-attributes.tab.keystream
│   │   │   │       │   │   │       ├── class-attributes.tab.keystream.len
│   │   │   │       │   │   │       ├── class-attributes.tab.len
│   │   │   │       │   │   │       ├── class-attributes.tab.values.at
│   │   │   │       │   │   │       ├── class-attributes.tab_i
│   │   │   │       │   │   │       ├── class-attributes.tab_i.len
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.len
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.values.at
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab_i
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab_i.len
│   │   │   │       │   │   │       ├── inline-functions.tab
│   │   │   │       │   │   │       ├── inline-functions.tab.keystream
│   │   │   │       │   │   │       ├── inline-functions.tab.keystream.len
│   │   │   │       │   │   │       ├── inline-functions.tab.len
│   │   │   │       │   │   │       ├── inline-functions.tab.values.at
│   │   │   │       │   │   │       ├── inline-functions.tab_i
│   │   │   │       │   │   │       ├── inline-functions.tab_i.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │       │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │       │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │       │   │   │       ├── proto.tab
│   │   │   │       │   │   │       ├── proto.tab.keystream
│   │   │   │       │   │   │       ├── proto.tab.keystream.len
│   │   │   │       │   │   │       ├── proto.tab.len
│   │   │   │       │   │   │       ├── proto.tab.values.at
│   │   │   │       │   │   │       ├── proto.tab_i
│   │   │   │       │   │   │       ├── proto.tab_i.len
│   │   │   │       │   │   │       ├── source-to-classes.tab
│   │   │   │       │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │       │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │       │   │   │       ├── source-to-classes.tab.len
│   │   │   │       │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │       │   │   │       ├── source-to-classes.tab_i
│   │   │   │       │   │   │       ├── source-to-classes.tab_i.len
│   │   │   │       │   │   │       ├── subtypes.tab
│   │   │   │       │   │   │       ├── subtypes.tab.keystream
│   │   │   │       │   │   │       ├── subtypes.tab.keystream.len
│   │   │   │       │   │   │       ├── subtypes.tab.len
│   │   │   │       │   │   │       ├── subtypes.tab.values.at
│   │   │   │       │   │   │       ├── subtypes.tab_i
│   │   │   │       │   │   │       ├── subtypes.tab_i.len
│   │   │   │       │   │   │       ├── supertypes.tab
│   │   │   │       │   │   │       ├── supertypes.tab.keystream
│   │   │   │       │   │   │       ├── supertypes.tab.keystream.len
│   │   │   │       │   │   │       ├── supertypes.tab.len
│   │   │   │       │   │   │       ├── supertypes.tab.values.at
│   │   │   │       │   │   │       ├── supertypes.tab_i
│   │   │   │       │   │   │       └── supertypes.tab_i.len
│   │   │   │       │   │   └── lookups/
│   │   │   │       │   │       ├── counters.tab
│   │   │   │       │   │       ├── file-to-id.tab
│   │   │   │       │   │       ├── file-to-id.tab.keystream
│   │   │   │       │   │       ├── file-to-id.tab.keystream.len
│   │   │   │       │   │       ├── file-to-id.tab.len
│   │   │   │       │   │       ├── file-to-id.tab.values.at
│   │   │   │       │   │       ├── file-to-id.tab_i
│   │   │   │       │   │       ├── file-to-id.tab_i.len
│   │   │   │       │   │       ├── id-to-file.tab
│   │   │   │       │   │       ├── id-to-file.tab.keystream
│   │   │   │       │   │       ├── id-to-file.tab.keystream.len
│   │   │   │       │   │       ├── id-to-file.tab.len
│   │   │   │       │   │       ├── id-to-file.tab.values.at
│   │   │   │       │   │       ├── id-to-file.tab_i
│   │   │   │       │   │       ├── id-to-file.tab_i.len
│   │   │   │       │   │       ├── lookups.tab
│   │   │   │       │   │       ├── lookups.tab.keystream
│   │   │   │       │   │       ├── lookups.tab.keystream.len
│   │   │   │       │   │       ├── lookups.tab.len
│   │   │   │       │   │       ├── lookups.tab.values.at
│   │   │   │       │   │       ├── lookups.tab_i
│   │   │   │       │   │       └── lookups.tab_i.len
│   │   │   │       │   └── last-build.bin
│   │   │   │       ├── classpath-snapshot/
│   │   │   │       │   └── shrunk-classpath-snapshot.bin
│   │   │   │       └── local-state/
│   │   │   ├── kspCaches/
│   │   │   │   ├── debug/
│   │   │   │   │   ├── backups/java/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideAuthControllerApiFactory.java
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideJsonFactory.java
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory.java
│   │   │   │   │   │   │   │   ├── NetworkModule_ProvideOkHttpClientFactory.java
│   │   │   │   │   │   │   │   └── NetworkModule_ProvideRetrofitFactory.java
│   │   │   │   │   │   │   ├── AuthInterceptor_Factory.java
│   │   │   │   │   │   │   └── TokenRefreshAuthenticator_Factory.java
│   │   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_core_network_di_NetworkModule.java
│   │   │   │   │   ├── logs/
│   │   │   │   │   ├── symbolLookups/
│   │   │   │   │   │   ├── counters.tab
│   │   │   │   │   │   ├── file-to-id.tab
│   │   │   │   │   │   ├── file-to-id.tab.keystream
│   │   │   │   │   │   ├── file-to-id.tab.keystream.len
│   │   │   │   │   │   ├── file-to-id.tab.len
│   │   │   │   │   │   ├── file-to-id.tab.values.at
│   │   │   │   │   │   ├── file-to-id.tab_i
│   │   │   │   │   │   ├── file-to-id.tab_i.len
│   │   │   │   │   │   ├── id-to-file.tab
│   │   │   │   │   │   ├── id-to-file.tab.keystream
│   │   │   │   │   │   ├── id-to-file.tab.keystream.len
│   │   │   │   │   │   ├── id-to-file.tab.len
│   │   │   │   │   │   ├── id-to-file.tab.values.at
│   │   │   │   │   │   ├── id-to-file.tab_i
│   │   │   │   │   │   ├── id-to-file.tab_i.len
│   │   │   │   │   │   ├── lookups.tab
│   │   │   │   │   │   ├── lookups.tab.keystream
│   │   │   │   │   │   ├── lookups.tab.keystream.len
│   │   │   │   │   │   ├── lookups.tab.len
│   │   │   │   │   │   ├── lookups.tab.values.at
│   │   │   │   │   │   ├── lookups.tab_i
│   │   │   │   │   │   └── lookups.tab_i.len
│   │   │   │   │   ├── ap-classpath-entries.bin
│   │   │   │   │   ├── caches.uptodate
│   │   │   │   │   ├── classpath-entries.bin
│   │   │   │   │   ├── classpath-structure.bin
│   │   │   │   │   ├── sealed
│   │   │   │   │   ├── sourceToOutputs
│   │   │   │   │   └── symbols
│   │   │   │   ├── debugUnitTest/
│   │   │   │   │   ├── logs/
│   │   │   │   │   ├── symbolLookups/
│   │   │   │   │   │   ├── counters.tab
│   │   │   │   │   │   ├── file-to-id.tab
│   │   │   │   │   │   ├── file-to-id.tab.keystream
│   │   │   │   │   │   ├── file-to-id.tab.keystream.len
│   │   │   │   │   │   ├── file-to-id.tab.len
│   │   │   │   │   │   ├── file-to-id.tab.values.at
│   │   │   │   │   │   ├── file-to-id.tab_i
│   │   │   │   │   │   ├── file-to-id.tab_i.len
│   │   │   │   │   │   ├── id-to-file.tab
│   │   │   │   │   │   ├── id-to-file.tab.keystream
│   │   │   │   │   │   ├── id-to-file.tab.keystream.len
│   │   │   │   │   │   ├── id-to-file.tab.len
│   │   │   │   │   │   ├── id-to-file.tab.values.at
│   │   │   │   │   │   ├── id-to-file.tab_i.len
│   │   │   │   │   │   ├── lookups.tab
│   │   │   │   │   │   ├── lookups.tab.keystream
│   │   │   │   │   │   ├── lookups.tab.keystream.len
│   │   │   │   │   │   ├── lookups.tab.len
│   │   │   │   │   │   ├── lookups.tab.values.at
│   │   │   │   │   │   ├── lookups.tab_i
│   │   │   │   │   │   └── lookups.tab_i.len
│   │   │   │   │   ├── ap-classpath-entries.bin
│   │   │   │   │   ├── caches.uptodate
│   │   │   │   │   ├── classpath-entries.bin
│   │   │   │   │   ├── classpath-structure.bin
│   │   │   │   │   ├── sealed
│   │   │   │   │   ├── sourceToOutputs
│   │   │   │   │   └── symbols
│   │   │   │   └── release/
│   │   │   │       ├── backups/java/
│   │   │   │       │   ├── com/miguelrodriguez19/safecube/core/network/
│   │   │   │       │   │   ├── di/
│   │   │   │       │   │   │   ├── NetworkModule_ProvideApiServiceFactory.java
│   │   │   │       │   │   │   ├── NetworkModule_ProvideJsonFactory.java
│   │   │   │       │   │   │   ├── NetworkModule_ProvideNetworkConfigFactory.java
│   │   │   │       │   │   │   ├── NetworkModule_ProvideOkHttpClientFactory.java
│   │   │   │       │   │   │   └── NetworkModule_ProvideRetrofitFactory.java
│   │   │   │       │   │   └── AuthInterceptor_Factory.java
│   │   │   │       │   └── hilt_aggregated_deps/
│   │   │   │       │       └── _com_miguelrodriguez19_safecube_core_network_di_NetworkModule.java
│   │   │   │       ├── logs/
│   │   │   │       ├── symbolLookups/
│   │   │   │       │   ├── counters.tab
│   │   │   │       │   ├── file-to-id.tab
│   │   │   │       │   ├── file-to-id.tab.keystream
│   │   │   │       │   ├── file-to-id.tab.keystream.len
│   │   │   │       │   ├── file-to-id.tab.len
│   │   │   │       │   ├── file-to-id.tab.values.at
│   │   │   │       │   ├── file-to-id.tab_i
│   │   │   │       │   ├── file-to-id.tab_i.len
│   │   │   │       │   ├── id-to-file.tab
│   │   │   │       │   ├── id-to-file.tab.keystream
│   │   │   │       │   ├── id-to-file.tab.keystream.len
│   │   │   │       │   ├── id-to-file.tab.len
│   │   │   │       │   ├── id-to-file.tab.values.at
│   │   │   │       │   ├── id-to-file.tab_i
│   │   │   │       │   ├── id-to-file.tab_i.len
│   │   │   │       │   ├── lookups.tab
│   │   │   │       │   ├── lookups.tab.keystream
│   │   │   │       │   ├── lookups.tab.keystream.len
│   │   │   │       │   ├── lookups.tab.len
│   │   │   │       │   ├── lookups.tab.values.at
│   │   │   │       │   ├── lookups.tab_i
│   │   │   │       │   └── lookups.tab_i.len
│   │   │   │       ├── ap-classpath-entries.bin
│   │   │   │       ├── caches.uptodate
│   │   │   │       ├── classpath-entries.bin
│   │   │   │       ├── classpath-structure.bin
│   │   │   │       ├── sealed
│   │   │   │       ├── sourceToOutputs
│   │   │   │       └── symbols
│   │   │   ├── outputs/
│   │   │   │   ├── aar/
│   │   │   │   │   └── network-debug.aar
│   │   │   │   ├── androidTest-results/connected/debug/
│   │   │   │   ├── apk/androidTest/debug/
│   │   │   │   │   ├── network-debug-androidTest.apk
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── code_coverage/debugAndroidTest/connected/
│   │   │   │   │   └── coverage.ec
│   │   │   │   ├── connected_android_test_additional_output/debugAndroidTest/connected/
│   │   │   │   └── logs/
│   │   │   │       ├── manifest-merger-debug-report.txt
│   │   │   │       └── manifest-merger-release-report.txt
│   │   │   ├── reports/
│   │   │   │   ├── androidTests/connected/debug/
│   │   │   │   │   ├── css/
│   │   │   │   │   │   ├── base-style.css
│   │   │   │   │   │   └── style.css
│   │   │   │   │   ├── js/
│   │   │   │   │   │   └── report.js
│   │   │   │   │   └── index.html
│   │   │   │   └── tests/testDebugUnitTest/
│   │   │   │       ├── classes/
│   │   │   │       │   └── com.miguelrodriguez19.safecube.core.network.NetworkClientFactoryTest.html
│   │   │   │       ├── css/
│   │   │   │       │   ├── base-style.css
│   │   │   │       │   └── style.css
│   │   │   │       ├── js/
│   │   │   │       │   └── report.js
│   │   │   │       ├── packages/
│   │   │   │       │   └── com.miguelrodriguez19.safecube.core.network.html
│   │   │   │       └── index.html
│   │   │   ├── test-results/testDebugUnitTest/
│   │   │   │   ├── binary/
│   │   │   │   │   ├── output.bin
│   │   │   │   │   ├── output.bin.idx
│   │   │   │   │   └── results.bin
│   │   │   │   └── TEST-com.miguelrodriguez19.safecube.core.network.NetworkClientFactoryTest.xml
│   │   │   └── tmp/
│   │   │       ├── compileDebugAndroidTestJavaWithJavac/
│   │   │       │   └── previous-compilation-data.bin
│   │   │       ├── compileDebugJavaWithJavac/
│   │   │       │   ├── compileTransaction/
│   │   │       │   │   ├── backup-dir/
│   │   │       │   │   └── stash-dir/
│   │   │       │   │       └── BuildConfig.class.uniqueId0
│   │   │       │   └── previous-compilation-data.bin
│   │   │       ├── compileReleaseJavaWithJavac/
│   │   │       │   └── previous-compilation-data.bin
│   │   │       └── testDebugUnitTest/
│   │   ├── openapi/
│   │   │   └── OpenAPI.json
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   ├── di/
│   │   │   │   │   │   └── NetworkModule.kt
│   │   │   │   │   ├── AuthInterceptor.kt
│   │   │   │   │   ├── NetworkClientFactory.kt
│   │   │   │   │   ├── NetworkConfig.kt
│   │   │   │   │   ├── TokenProvider.kt
│   │   │   │   │   └── TokenRefreshAuthenticator.kt
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/java/com/miguelrodriguez19/safecube/core/network/
│   │   │       └── NetworkClientFactoryTest.kt
│   │   │   │   └── build.gradle.kts
│   ├── storage/
│   │   ├── build/
│   │   │   ├── .transforms/
│   │   │   │   ├── 1735c218071b5e3ea028b947d94817ac/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 25d22a6c13c963b9bc29170eb339d021/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 2ff1ab6e058ce2d6e07ad9b66eaab5ff/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/storage/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   ├── StorageModule.dex
│   │   │   │   │   │   │   │   ├── StorageModule_ProvideAppDatabaseFactory.dex
│   │   │   │   │   │   │   │   └── StorageModule_ProvideSecureItemDaoFactory.dex
│   │   │   │   │   │   │   ├── AppDatabase.dex
│   │   │   │   │   │   │   ├── AppDatabase_Impl$createOpenDelegate$_openDelegate$1.dex
│   │   │   │   │   │   │   ├── AppDatabase_Impl.dex
│   │   │   │   │   │   │   ├── SecureItemDao.dex
│   │   │   │   │   │   │   ├── SecureItemDao_Impl$Companion.dex
│   │   │   │   │   │   │   ├── SecureItemDao_Impl.dex
│   │   │   │   │   │   │   └── SecureItemEntity.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_storage_di_StorageModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 51063f48a3871669413ac9e220febf2f/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 66d07e258dbe65733fe4082e53db4c6e/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 70e614eb1ee64b707c715e936b6c5009/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 7791e7087eab60523e700c9c0218947e/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 9b32b576530c7286fe13ef70b3df6c3f/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/storage/di/
│   │   │   │   │   │   │   └── StorageModule.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_storage_di_StorageModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── b48166aac31ac09b1a5c286e5b752427/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/storage/di/
│   │   │   │   │   │   │   └── StorageModule.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_storage_di_StorageModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── d0f855ea3d05aed7fc4379ace50f6bfc/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── fa56d080bac8bfd991b0a1b320b9f699/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/storage/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   ├── StorageModule.dex
│   │   │   │   │   │   │   │   ├── StorageModule_ProvideAppDatabaseFactory.dex
│   │   │   │   │   │   │   │   └── StorageModule_ProvideSecureItemDaoFactory.dex
│   │   │   │   │   │   │   ├── AppDatabase.dex
│   │   │   │   │   │   │   ├── AppDatabase_Impl$createOpenDelegate$_openDelegate$1.dex
│   │   │   │   │   │   │   ├── AppDatabase_Impl.dex
│   │   │   │   │   │   │   ├── SecureItemDao.dex
│   │   │   │   │   │   │   ├── SecureItemDao_Impl$Companion.dex
│   │   │   │   │   │   │   ├── SecureItemDao_Impl.dex
│   │   │   │   │   │   │   └── SecureItemEntity.dex
│   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_storage_di_StorageModule.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   └── fca67f844d70b2a78f8a48623a5d10b1/
│   │   │   │       ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │       │   └── desugar_graph.bin
│   │   │   │       └── results.bin
│   │   │   ├── generated/
│   │   │   │   ├── ap_generated_sources/
│   │   │   │   │   ├── debug/out/
│   │   │   │   │   └── release/out/
│   │   │   │   ├── ksp/
│   │   │   │   │   ├── debug/
│   │   │   │   │   │   ├── java/
│   │   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/storage/di/
│   │   │   │   │   │   │   │   ├── StorageModule_ProvideAppDatabaseFactory.java
│   │   │   │   │   │   │   │   └── StorageModule_ProvideSecureItemDaoFactory.java
│   │   │   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_core_storage_di_StorageModule.java
│   │   │   │   │   │   └── kotlin/com/miguelrodriguez19/safecube/core/storage/
│   │   │   │   │   │       ├── AppDatabase_Impl.kt
│   │   │   │   │   │       └── SecureItemDao_Impl.kt
│   │   │   │   │   └── release/
│   │   │   │   │       ├── java/
│   │   │   │   │       │   ├── com/miguelrodriguez19/safecube/core/storage/di/
│   │   │   │   │       │   │   ├── StorageModule_ProvideAppDatabaseFactory.java
│   │   │   │   │       │   │   └── StorageModule_ProvideSecureItemDaoFactory.java
│   │   │   │   │       │   └── hilt_aggregated_deps/
│   │   │   │   │       │       └── _com_miguelrodriguez19_safecube_core_storage_di_StorageModule.java
│   │   │   │   │       └── kotlin/com/miguelrodriguez19/safecube/core/storage/
│   │   │   │   │           ├── AppDatabase_Impl.kt
│   │   │   │   │           └── SecureItemDao_Impl.kt
│   │   │   │   ├── res/pngs/
│   │   │   │   │   ├── debug/
│   │   │   │   │   └── release/
│   │   │   │   └── updated_navigation_xml/
│   │   │   │       ├── debug/
│   │   │   │       ├── debugAndroidTest/
│   │   │   │       └── release/
│   │   │   ├── intermediates/
│   │   │   │   ├── aapt_friendly_merged_manifests/
│   │   │   │   │   ├── debug/processDebugManifest/aapt/
│   │   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   │   └── output-metadata.json
│   │   │   │   │   └── release/processReleaseManifest/aapt/
│   │   │   │   │       ├── AndroidManifest.xml
│   │   │   │   │       └── output-metadata.json
│   │   │   │   ├── aar_libs_directory/debug/syncDebugLibJars/libs/
│   │   │   │   ├── aar_main_jar/debug/syncDebugLibJars/
│   │   │   │   │   └── classes.jar
│   │   │   │   ├── aar_metadata/
│   │   │   │   │   ├── debug/writeDebugAarMetadata/
│   │   │   │   │   │   │   │   │   │   │   └── release/writeReleaseAarMetadata/
│   │   │   │   │       │   │   │   │   ├── aar_metadata_check/
│   │   │   │   │   ├── debug/checkDebugAarMetadata/
│   │   │   │   │   └── debugAndroidTest/checkDebugAndroidTestAarMetadata/
│   │   │   │   ├── android_res_source_set_path_map/debugAndroidTest/mapDebugAndroidTestSourceSetPaths/
│   │   │   │   │   └── file-map.txt
│   │   │   │   ├── annotation_processor_list/
│   │   │   │   │   ├── debug/javaPreCompileDebug/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   ├── debugAndroidTest/javaPreCompileDebugAndroidTest/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   ├── debugUnitTest/javaPreCompileDebugUnitTest/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   └── release/javaPreCompileRelease/
│   │   │   │   │       └── annotationProcessors.json
│   │   │   │   ├── annotations_typedef_file/debug/extractDebugAnnotations/
│   │   │   │   │   └── typedefs.txt
│   │   │   │   ├── annotations_zip/debug/extractDebugAnnotations/
│   │   │   │   ├── apk_ide_redirect_file/debugAndroidTest/createDebugAndroidTestApkListingFileRedirect/
│   │   │   │   │   └── redirect.txt
│   │   │   │   ├── assets/
│   │   │   │   │   ├── debug/mergeDebugAssets/
│   │   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestAssets/
│   │   │   │   │   └── release/mergeReleaseAssets/
│   │   │   │   ├── built_in_kotlinc/
│   │   │   │   │   ├── debug/compileDebugKotlin/classes/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/storage/
│   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   └── StorageModule.class
│   │   │   │   │   │   │   ├── AppDatabase.class
│   │   │   │   │   │   │   ├── AppDatabase_Impl$createOpenDelegate$_openDelegate$1.class
│   │   │   │   │   │   │   ├── AppDatabase_Impl.class
│   │   │   │   │   │   │   ├── SecureItemDao.class
│   │   │   │   │   │   │   ├── SecureItemDao_Impl$Companion.class
│   │   │   │   │   │   │   ├── SecureItemDao_Impl.class
│   │   │   │   │   │   │   └── SecureItemEntity.class
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── storage.kotlin_module
│   │   │   │   │   └── release/compileReleaseKotlin/classes/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/storage/
│   │   │   │   │       │   ├── di/
│   │   │   │   │       │   │   └── StorageModule.class
│   │   │   │   │       │   ├── AppDatabase.class
│   │   │   │   │       │   ├── AppDatabase_Impl$createOpenDelegate$_openDelegate$1.class
│   │   │   │   │       │   ├── AppDatabase_Impl.class
│   │   │   │   │       │   ├── SecureItemDao.class
│   │   │   │   │       │   ├── SecureItemDao_Impl$Companion.class
│   │   │   │   │       │   ├── SecureItemDao_Impl.class
│   │   │   │   │       │   └── SecureItemEntity.class
│   │   │   │   │       └── META-INF/
│   │   │   │   │           └── storage.kotlin_module
│   │   │   │   ├── classes/
│   │   │   │   │   ├── debug/transformDebugClassesWithAsm/
│   │   │   │   │   │   ├── dirs/
│   │   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/storage/
│   │   │   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   │   │   ├── StorageModule.class
│   │   │   │   │   │   │   │   │   ├── StorageModule_ProvideAppDatabaseFactory.class
│   │   │   │   │   │   │   │   │   └── StorageModule_ProvideSecureItemDaoFactory.class
│   │   │   │   │   │   │   │   ├── AppDatabase.class
│   │   │   │   │   │   │   │   ├── AppDatabase_Impl$createOpenDelegate$_openDelegate$1.class
│   │   │   │   │   │   │   │   ├── AppDatabase_Impl.class
│   │   │   │   │   │   │   │   ├── SecureItemDao.class
│   │   │   │   │   │   │   │   ├── SecureItemDao_Impl$Companion.class
│   │   │   │   │   │   │   │   ├── SecureItemDao_Impl.class
│   │   │   │   │   │   │   │   └── SecureItemEntity.class
│   │   │   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_storage_di_StorageModule.class
│   │   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │   │       └── storage.kotlin_module
│   │   │   │   │   │   └── jars/
│   │   │   │   │   └── release/transformReleaseClassesWithAsm/
│   │   │   │   │       ├── dirs/
│   │   │   │   │       │   ├── com/miguelrodriguez19/safecube/core/storage/
│   │   │   │   │       │   │   ├── di/
│   │   │   │   │       │   │   │   ├── StorageModule.class
│   │   │   │   │       │   │   │   ├── StorageModule_ProvideAppDatabaseFactory.class
│   │   │   │   │       │   │   │   └── StorageModule_ProvideSecureItemDaoFactory.class
│   │   │   │   │       │   │   ├── AppDatabase.class
│   │   │   │   │       │   │   ├── AppDatabase_Impl$createOpenDelegate$_openDelegate$1.class
│   │   │   │   │       │   │   ├── AppDatabase_Impl.class
│   │   │   │   │       │   │   ├── SecureItemDao.class
│   │   │   │   │       │   │   ├── SecureItemDao_Impl$Companion.class
│   │   │   │   │       │   │   ├── SecureItemDao_Impl.class
│   │   │   │   │       │   │   └── SecureItemEntity.class
│   │   │   │   │       │   ├── hilt_aggregated_deps/
│   │   │   │   │       │   │   └── _com_miguelrodriguez19_safecube_core_storage_di_StorageModule.class
│   │   │   │   │       │   └── META-INF/
│   │   │   │   │       │       └── storage.kotlin_module
│   │   │   │   │       └── jars/
│   │   │   │   ├── compile_and_runtime_r_class_jar/
│   │   │   │   │   ├── debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   └── debugUnitTest/generateDebugUnitTestStubRFile/
│   │   │   │   │       └── R.jar
│   │   │   │   ├── compile_library_classes_jar/
│   │   │   │   │   ├── debug/bundleLibCompileToJarDebug/
│   │   │   │   │   │   └── classes.jar
│   │   │   │   │   └── release/bundleLibCompileToJarRelease/
│   │   │   │   │       └── classes.jar
│   │   │   │   ├── compile_r_class_jar/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── R.jar
│   │   │   │   ├── compile_symbol_list/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── R.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── R.txt
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── R.txt
│   │   │   │   ├── compiled_local_resources/debug/compileDebugLibraryResources/out/
│   │   │   │   ├── compiled_navigation_res/debugAndroidTest/compileDebugAndroidTestNavigationResources/
│   │   │   │   ├── compressed_assets/debugAndroidTest/compressDebugAndroidTestAssets/out/
│   │   │   │   ├── consumer_proguard_dir/release/
│   │   │   │   ├── data_binding_layout_info_type_merge/debugAndroidTest/mergeDebugAndroidTestResources/out/
│   │   │   │   ├── data_binding_layout_info_type_package/
│   │   │   │   │   ├── debug/packageDebugResources/out/
│   │   │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/out/
│   │   │   │   │   └── release/packageReleaseResources/out/
│   │   │   │   ├── desugar_graph/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   │   ├── currentProject/
│   │   │   │   │   │   ├── jar_51221788753efbb03af870c5919611aa7bc1f8290bb611c217687293b7387e21_bucket_0/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_51221788753efbb03af870c5919611aa7bc1f8290bb611c217687293b7387e21_bucket_1/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_51221788753efbb03af870c5919611aa7bc1f8290bb611c217687293b7387e21_bucket_2/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_51221788753efbb03af870c5919611aa7bc1f8290bb611c217687293b7387e21_bucket_3/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_51221788753efbb03af870c5919611aa7bc1f8290bb611c217687293b7387e21_bucket_4/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   └── jar_51221788753efbb03af870c5919611aa7bc1f8290bb611c217687293b7387e21_bucket_5/
│   │   │   │   │   │       └── graph.bin
│   │   │   │   │   ├── externalLibs/
│   │   │   │   │   ├── mixedScopes/
│   │   │   │   │   └── otherProjects/
│   │   │   │   ├── dex/debugAndroidTest/
│   │   │   │   │   ├── mergeExtDexDebugAndroidTest/
│   │   │   │   │   │   └── classes.dex
│   │   │   │   │   ├── mergeLibDexDebugAndroidTest/
│   │   │   │   │   │   ├── 0/
│   │   │   │   │   │   ├── 1/
│   │   │   │   │   │   ├── 10/
│   │   │   │   │   │   ├── 11/
│   │   │   │   │   │   ├── 12/
│   │   │   │   │   │   ├── 13/
│   │   │   │   │   │   ├── 14/
│   │   │   │   │   │   ├── 15/
│   │   │   │   │   │   ├── 2/
│   │   │   │   │   │   ├── 3/
│   │   │   │   │   │   ├── 4/
│   │   │   │   │   │   ├── 5/
│   │   │   │   │   │   ├── 6/
│   │   │   │   │   │   ├── 7/
│   │   │   │   │   │   ├── 8/
│   │   │   │   │   │   └── 9/
│   │   │   │   │   └── mergeProjectDexDebugAndroidTest/
│   │   │   │   │       ├── 0/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 1/
│   │   │   │   │       ├── 10/
│   │   │   │   │       ├── 11/
│   │   │   │   │       ├── 12/
│   │   │   │   │       ├── 13/
│   │   │   │   │       ├── 14/
│   │   │   │   │       ├── 15/
│   │   │   │   │       ├── 2/
│   │   │   │   │       ├── 3/
│   │   │   │   │       ├── 4/
│   │   │   │   │       ├── 5/
│   │   │   │   │       ├── 6/
│   │   │   │   │       ├── 7/
│   │   │   │   │       ├── 8/
│   │   │   │   │       └── 9/
│   │   │   │   ├── dex_archive_input_jar_hashes/debugAndroidTest/dexBuilderDebugAndroidTest/
│   │   │   │   │   └── out
│   │   │   │   ├── dex_number_of_buckets_file/debugAndroidTest/dexBuilderDebugAndroidTest/
│   │   │   │   │   └── out
│   │   │   │   ├── duplicate_classes_check/debugAndroidTest/checkDebugAndroidTestDuplicateClasses/
│   │   │   │   ├── external_file_lib_dex_archives/debugAndroidTest/desugarDebugAndroidTestFileDependencies/
│   │   │   │   ├── external_libs_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── external_libs_dex_archive_with_artifact_transforms/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── generated_proguard_file/
│   │   │   │   │   ├── debug/mergeDebugGeneratedProguardFiles/
│   │   │   │   │   └── release/mergeReleaseGeneratedProguardFiles/
│   │   │   │   ├── incremental/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── debug-mergeJavaRes/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   └── merge-state
│   │   │   │   │   ├── debugAndroidTest/
│   │   │   │   │   │   ├── mergeDebugAndroidTestResources/
│   │   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   │   │   ├── values/
│   │   │   │   │   │   │   │   │   └── values.xml
│   │   │   │   │   │   │   │   ├── values-af/
│   │   │   │   │   │   │   │   │   └── values-af.xml
│   │   │   │   │   │   │   │   ├── values-am/
│   │   │   │   │   │   │   │   │   └── values-am.xml
│   │   │   │   │   │   │   │   ├── values-ar/
│   │   │   │   │   │   │   │   │   └── values-ar.xml
│   │   │   │   │   │   │   │   ├── values-as/
│   │   │   │   │   │   │   │   │   └── values-as.xml
│   │   │   │   │   │   │   │   ├── values-az/
│   │   │   │   │   │   │   │   │   └── values-az.xml
│   │   │   │   │   │   │   │   ├── values-b+sr+Latn/
│   │   │   │   │   │   │   │   │   └── values-b+sr+Latn.xml
│   │   │   │   │   │   │   │   ├── values-be/
│   │   │   │   │   │   │   │   │   └── values-be.xml
│   │   │   │   │   │   │   │   ├── values-bg/
│   │   │   │   │   │   │   │   │   └── values-bg.xml
│   │   │   │   │   │   │   │   ├── values-bn/
│   │   │   │   │   │   │   │   │   └── values-bn.xml
│   │   │   │   │   │   │   │   ├── values-bs/
│   │   │   │   │   │   │   │   │   └── values-bs.xml
│   │   │   │   │   │   │   │   ├── values-ca/
│   │   │   │   │   │   │   │   │   └── values-ca.xml
│   │   │   │   │   │   │   │   ├── values-cs/
│   │   │   │   │   │   │   │   │   └── values-cs.xml
│   │   │   │   │   │   │   │   ├── values-da/
│   │   │   │   │   │   │   │   │   └── values-da.xml
│   │   │   │   │   │   │   │   ├── values-de/
│   │   │   │   │   │   │   │   │   └── values-de.xml
│   │   │   │   │   │   │   │   ├── values-el/
│   │   │   │   │   │   │   │   │   └── values-el.xml
│   │   │   │   │   │   │   │   ├── values-en-rAU/
│   │   │   │   │   │   │   │   │   └── values-en-rAU.xml
│   │   │   │   │   │   │   │   ├── values-en-rCA/
│   │   │   │   │   │   │   │   │   └── values-en-rCA.xml
│   │   │   │   │   │   │   │   ├── values-en-rGB/
│   │   │   │   │   │   │   │   │   └── values-en-rGB.xml
│   │   │   │   │   │   │   │   ├── values-en-rIN/
│   │   │   │   │   │   │   │   │   └── values-en-rIN.xml
│   │   │   │   │   │   │   │   ├── values-en-rXC/
│   │   │   │   │   │   │   │   │   └── values-en-rXC.xml
│   │   │   │   │   │   │   │   ├── values-es/
│   │   │   │   │   │   │   │   │   └── values-es.xml
│   │   │   │   │   │   │   │   ├── values-es-rUS/
│   │   │   │   │   │   │   │   │   └── values-es-rUS.xml
│   │   │   │   │   │   │   │   ├── values-et/
│   │   │   │   │   │   │   │   │   └── values-et.xml
│   │   │   │   │   │   │   │   ├── values-eu/
│   │   │   │   │   │   │   │   │   └── values-eu.xml
│   │   │   │   │   │   │   │   ├── values-fa/
│   │   │   │   │   │   │   │   │   └── values-fa.xml
│   │   │   │   │   │   │   │   ├── values-fi/
│   │   │   │   │   │   │   │   │   └── values-fi.xml
│   │   │   │   │   │   │   │   ├── values-fr/
│   │   │   │   │   │   │   │   │   └── values-fr.xml
│   │   │   │   │   │   │   │   ├── values-fr-rCA/
│   │   │   │   │   │   │   │   │   └── values-fr-rCA.xml
│   │   │   │   │   │   │   │   ├── values-gl/
│   │   │   │   │   │   │   │   │   └── values-gl.xml
│   │   │   │   │   │   │   │   ├── values-gu/
│   │   │   │   │   │   │   │   │   └── values-gu.xml
│   │   │   │   │   │   │   │   ├── values-hi/
│   │   │   │   │   │   │   │   │   └── values-hi.xml
│   │   │   │   │   │   │   │   ├── values-hr/
│   │   │   │   │   │   │   │   │   └── values-hr.xml
│   │   │   │   │   │   │   │   ├── values-hu/
│   │   │   │   │   │   │   │   │   └── values-hu.xml
│   │   │   │   │   │   │   │   ├── values-hy/
│   │   │   │   │   │   │   │   │   └── values-hy.xml
│   │   │   │   │   │   │   │   ├── values-in/
│   │   │   │   │   │   │   │   │   └── values-in.xml
│   │   │   │   │   │   │   │   ├── values-is/
│   │   │   │   │   │   │   │   │   └── values-is.xml
│   │   │   │   │   │   │   │   ├── values-it/
│   │   │   │   │   │   │   │   │   └── values-it.xml
│   │   │   │   │   │   │   │   ├── values-iw/
│   │   │   │   │   │   │   │   │   └── values-iw.xml
│   │   │   │   │   │   │   │   ├── values-ja/
│   │   │   │   │   │   │   │   │   └── values-ja.xml
│   │   │   │   │   │   │   │   ├── values-ka/
│   │   │   │   │   │   │   │   │   └── values-ka.xml
│   │   │   │   │   │   │   │   ├── values-kk/
│   │   │   │   │   │   │   │   │   └── values-kk.xml
│   │   │   │   │   │   │   │   ├── values-km/
│   │   │   │   │   │   │   │   │   └── values-km.xml
│   │   │   │   │   │   │   │   ├── values-kn/
│   │   │   │   │   │   │   │   │   └── values-kn.xml
│   │   │   │   │   │   │   │   ├── values-ko/
│   │   │   │   │   │   │   │   │   └── values-ko.xml
│   │   │   │   │   │   │   │   ├── values-ky/
│   │   │   │   │   │   │   │   │   └── values-ky.xml
│   │   │   │   │   │   │   │   ├── values-lo/
│   │   │   │   │   │   │   │   │   └── values-lo.xml
│   │   │   │   │   │   │   │   ├── values-lt/
│   │   │   │   │   │   │   │   │   └── values-lt.xml
│   │   │   │   │   │   │   │   ├── values-lv/
│   │   │   │   │   │   │   │   │   └── values-lv.xml
│   │   │   │   │   │   │   │   ├── values-mk/
│   │   │   │   │   │   │   │   │   └── values-mk.xml
│   │   │   │   │   │   │   │   ├── values-ml/
│   │   │   │   │   │   │   │   │   └── values-ml.xml
│   │   │   │   │   │   │   │   ├── values-mn/
│   │   │   │   │   │   │   │   │   └── values-mn.xml
│   │   │   │   │   │   │   │   ├── values-mr/
│   │   │   │   │   │   │   │   │   └── values-mr.xml
│   │   │   │   │   │   │   │   ├── values-ms/
│   │   │   │   │   │   │   │   │   └── values-ms.xml
│   │   │   │   │   │   │   │   ├── values-my/
│   │   │   │   │   │   │   │   │   └── values-my.xml
│   │   │   │   │   │   │   │   ├── values-nb/
│   │   │   │   │   │   │   │   │   └── values-nb.xml
│   │   │   │   │   │   │   │   ├── values-ne/
│   │   │   │   │   │   │   │   │   └── values-ne.xml
│   │   │   │   │   │   │   │   ├── values-nl/
│   │   │   │   │   │   │   │   │   └── values-nl.xml
│   │   │   │   │   │   │   │   ├── values-or/
│   │   │   │   │   │   │   │   │   └── values-or.xml
│   │   │   │   │   │   │   │   ├── values-pa/
│   │   │   │   │   │   │   │   │   └── values-pa.xml
│   │   │   │   │   │   │   │   ├── values-pl/
│   │   │   │   │   │   │   │   │   └── values-pl.xml
│   │   │   │   │   │   │   │   ├── values-pt/
│   │   │   │   │   │   │   │   │   └── values-pt.xml
│   │   │   │   │   │   │   │   ├── values-pt-rBR/
│   │   │   │   │   │   │   │   │   └── values-pt-rBR.xml
│   │   │   │   │   │   │   │   ├── values-pt-rPT/
│   │   │   │   │   │   │   │   │   └── values-pt-rPT.xml
│   │   │   │   │   │   │   │   ├── values-ro/
│   │   │   │   │   │   │   │   │   └── values-ro.xml
│   │   │   │   │   │   │   │   ├── values-ru/
│   │   │   │   │   │   │   │   │   └── values-ru.xml
│   │   │   │   │   │   │   │   ├── values-si/
│   │   │   │   │   │   │   │   │   └── values-si.xml
│   │   │   │   │   │   │   │   ├── values-sk/
│   │   │   │   │   │   │   │   │   └── values-sk.xml
│   │   │   │   │   │   │   │   ├── values-sl/
│   │   │   │   │   │   │   │   │   └── values-sl.xml
│   │   │   │   │   │   │   │   ├── values-sq/
│   │   │   │   │   │   │   │   │   └── values-sq.xml
│   │   │   │   │   │   │   │   ├── values-sr/
│   │   │   │   │   │   │   │   │   └── values-sr.xml
│   │   │   │   │   │   │   │   ├── values-sv/
│   │   │   │   │   │   │   │   │   └── values-sv.xml
│   │   │   │   │   │   │   │   ├── values-sw/
│   │   │   │   │   │   │   │   │   └── values-sw.xml
│   │   │   │   │   │   │   │   ├── values-ta/
│   │   │   │   │   │   │   │   │   └── values-ta.xml
│   │   │   │   │   │   │   │   ├── values-te/
│   │   │   │   │   │   │   │   │   └── values-te.xml
│   │   │   │   │   │   │   │   ├── values-th/
│   │   │   │   │   │   │   │   │   └── values-th.xml
│   │   │   │   │   │   │   │   ├── values-tl/
│   │   │   │   │   │   │   │   │   └── values-tl.xml
│   │   │   │   │   │   │   │   ├── values-tr/
│   │   │   │   │   │   │   │   │   └── values-tr.xml
│   │   │   │   │   │   │   │   ├── values-uk/
│   │   │   │   │   │   │   │   │   └── values-uk.xml
│   │   │   │   │   │   │   │   ├── values-ur/
│   │   │   │   │   │   │   │   │   └── values-ur.xml
│   │   │   │   │   │   │   │   ├── values-uz/
│   │   │   │   │   │   │   │   │   └── values-uz.xml
│   │   │   │   │   │   │   │   ├── values-v21/
│   │   │   │   │   │   │   │   │   └── values-v21.xml
│   │   │   │   │   │   │   │   ├── values-vi/
│   │   │   │   │   │   │   │   │   └── values-vi.xml
│   │   │   │   │   │   │   │   ├── values-zh-rCN/
│   │   │   │   │   │   │   │   │   └── values-zh-rCN.xml
│   │   │   │   │   │   │   │   ├── values-zh-rHK/
│   │   │   │   │   │   │   │   │   └── values-zh-rHK.xml
│   │   │   │   │   │   │   │   ├── values-zh-rTW/
│   │   │   │   │   │   │   │   │   └── values-zh-rTW.xml
│   │   │   │   │   │   │   │   └── values-zu/
│   │   │   │   │   │   │   │       └── values-zu.xml
│   │   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   │   └── packageDebugAndroidTestResources/
│   │   │   │   │   │       ├── merged.dir/
│   │   │   │   │   │       ├── stripped.dir/
│   │   │   │   │   │       │   │   │   │   │   │       └── merger.xml
│   │   │   │   │   ├── debugAndroidTest-mergeJavaRes/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   │   ├── 1ZuCeLYWmYw5MfiXYYnDIw==
│   │   │   │   │   │   │   ├── 3VJ0q7BGqqlcLs7MI_dHlw==
│   │   │   │   │   │   │   ├── 7trlVWptO4XEuV+Pwn+FJg==
│   │   │   │   │   │   │   ├── eAb6b17AoioR+wuvsUlXUg==
│   │   │   │   │   │   │   ├── EKfNJDNp1qej0g_hpuBvyw==
│   │   │   │   │   │   │   ├── fbJN1jTpNAQTBDjunGpNNw==
│   │   │   │   │   │   │   ├── gMUj5N1rwbVTZNnhBOwS4w==
│   │   │   │   │   │   │   ├── i1fywGgR7739mcd2F7zSGg==
│   │   │   │   │   │   │   ├── iAPbN5fNT6HNA+q7sMeHEw==
│   │   │   │   │   │   │   ├── IO63ZVBDFar17KQf_eKhoQ==
│   │   │   │   │   │   │   ├── J7CAnpdxVb_3lbUfaCbsMg==
│   │   │   │   │   │   │   ├── jrJKw3qGpVbyDMBUg69i1A==
│   │   │   │   │   │   │   ├── kBPjyU0m7mBApb7pIiwI2Q==
│   │   │   │   │   │   │   ├── Kws7Ph2jc1f3d_ecLLw8yA==
│   │   │   │   │   │   │   ├── L+aTIiZM60Y12ESzMr+bBQ==
│   │   │   │   │   │   │   ├── m7ifgdNNq+vMZDPtmarc1Q==
│   │   │   │   │   │   │   ├── mKewgQLdnB2MM0aEYmp3EQ==
│   │   │   │   │   │   │   ├── qD5dgASdHa8TAOro2Bvrtw==
│   │   │   │   │   │   │   ├── qOLoLN95hFQC6sku5Qy+CQ==
│   │   │   │   │   │   │   ├── rrna0K8qZsqmJGvstjZKCQ==
│   │   │   │   │   │   │   ├── tL2pp5i9_KZCC65aeIVhDg==
│   │   │   │   │   │   │   └── v1FFNCoEO5or6_tvb1ZiNA==
│   │   │   │   │   │   └── merge-state
│   │   │   │   │   ├── mergeDebugAndroidTestAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugAndroidTestJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeReleaseAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeReleaseJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── packageDebugAndroidTest/tmp/debugAndroidTest/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   │   ├── androidResources
│   │   │   │   │   │   │   └── javaResources0
│   │   │   │   │   │   └── dex-renamer-state.txt
│   │   │   │   │   ├── release/packageReleaseResources/
│   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── transformDebugClassesWithAsm/
│   │   │   │   │   └── transformReleaseClassesWithAsm/
│   │   │   │   ├── java_res/
│   │   │   │   │   ├── debug/processDebugJavaRes/out/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/storage/di/
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── storage.kotlin_module
│   │   │   │   │   └── release/processReleaseJavaRes/out/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/storage/di/
│   │   │   │   │       └── META-INF/
│   │   │   │   │           └── storage.kotlin_module
│   │   │   │   ├── javac/
│   │   │   │   │   ├── debug/compileDebugJavaWithJavac/classes/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/storage/di/
│   │   │   │   │   │   │   ├── StorageModule_ProvideAppDatabaseFactory.class
│   │   │   │   │   │   │   └── StorageModule_ProvideSecureItemDaoFactory.class
│   │   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_core_storage_di_StorageModule.class
│   │   │   │   │   └── release/compileReleaseJavaWithJavac/classes/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/core/storage/di/
│   │   │   │   │       │   ├── StorageModule_ProvideAppDatabaseFactory.class
│   │   │   │   │       │   └── StorageModule_ProvideSecureItemDaoFactory.class
│   │   │   │   │       └── hilt_aggregated_deps/
│   │   │   │   │           └── _com_miguelrodriguez19_safecube_core_storage_di_StorageModule.class
│   │   │   │   ├── library_and_local_jars_jni/debug/copyDebugJniLibsProjectAndLocalJars/jni/
│   │   │   │   ├── library_art_profile/
│   │   │   │   │   ├── debug/prepareDebugArtProfile/
│   │   │   │   │   └── release/prepareReleaseArtProfile/
│   │   │   │   ├── library_jni/
│   │   │   │   │   ├── debug/copyDebugJniLibsProjectOnly/jni/
│   │   │   │   │   └── release/copyReleaseJniLibsProjectOnly/jni/
│   │   │   │   ├── linked_resources_binary_format/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   ├── linked-resources-binary-format.ap_
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── lint_publish_jar/global/prepareLintJarForPublish/
│   │   │   │   ├── local_only_symbol_list/
│   │   │   │   │   ├── debug/parseDebugLocalResources/
│   │   │   │   │   │   └── R-def.txt
│   │   │   │   │   ├── debugAndroidTest/parseDebugAndroidTestLocalResources/
│   │   │   │   │   │   └── R-def.txt
│   │   │   │   │   └── release/parseReleaseLocalResources/
│   │   │   │   │       └── R-def.txt
│   │   │   │   ├── manifest_merge_blame_file/
│   │   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   │   └── manifest-merger-blame-debug-report.txt
│   │   │   │   │   ├── debugAndroidTest/processDebugAndroidTestManifest/
│   │   │   │   │   │   └── manifest-merger-blame-debug-androidTest-report.txt
│   │   │   │   │   └── release/processReleaseManifest/
│   │   │   │   │       └── manifest-merger-blame-release-report.txt
│   │   │   │   ├── merged_consumer_proguard_file/debug/mergeDebugConsumerProguardFiles/
│   │   │   │   ├── merged_java_res/
│   │   │   │   │   ├── debug/mergeDebugJavaResource/
│   │   │   │   │   │   └── feature-storage.jar
│   │   │   │   │   └── debugAndroidTest/mergeDebugAndroidTestJavaResource/
│   │   │   │   │       └── feature-storage.jar
│   │   │   │   ├── merged_jni_libs/
│   │   │   │   │   ├── debug/mergeDebugJniLibFolders/out/
│   │   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestJniLibFolders/out/
│   │   │   │   │   └── release/mergeReleaseJniLibFolders/out/
│   │   │   │   ├── merged_manifest/
│   │   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   │   └── AndroidManifest.xml
│   │   │   │   │   └── release/processReleaseManifest/
│   │   │   │   │       └── AndroidManifest.xml
│   │   │   │   ├── merged_res/debugAndroidTest/mergeDebugAndroidTestResources/
│   │   │   │   │   ├── values-af_values-af.arsc.flat
│   │   │   │   │   ├── values-am_values-am.arsc.flat
│   │   │   │   │   ├── values-ar_values-ar.arsc.flat
│   │   │   │   │   ├── values-as_values-as.arsc.flat
│   │   │   │   │   ├── values-az_values-az.arsc.flat
│   │   │   │   │   ├── values-b+sr+Latn_values-b+sr+Latn.arsc.flat
│   │   │   │   │   ├── values-be_values-be.arsc.flat
│   │   │   │   │   ├── values-bg_values-bg.arsc.flat
│   │   │   │   │   ├── values-bn_values-bn.arsc.flat
│   │   │   │   │   ├── values-bs_values-bs.arsc.flat
│   │   │   │   │   ├── values-ca_values-ca.arsc.flat
│   │   │   │   │   ├── values-cs_values-cs.arsc.flat
│   │   │   │   │   ├── values-da_values-da.arsc.flat
│   │   │   │   │   ├── values-de_values-de.arsc.flat
│   │   │   │   │   ├── values-el_values-el.arsc.flat
│   │   │   │   │   ├── values-en-rAU_values-en-rAU.arsc.flat
│   │   │   │   │   ├── values-en-rCA_values-en-rCA.arsc.flat
│   │   │   │   │   ├── values-en-rGB_values-en-rGB.arsc.flat
│   │   │   │   │   ├── values-en-rIN_values-en-rIN.arsc.flat
│   │   │   │   │   ├── values-en-rXC_values-en-rXC.arsc.flat
│   │   │   │   │   ├── values-es-rUS_values-es-rUS.arsc.flat
│   │   │   │   │   ├── values-es_values-es.arsc.flat
│   │   │   │   │   ├── values-et_values-et.arsc.flat
│   │   │   │   │   ├── values-eu_values-eu.arsc.flat
│   │   │   │   │   ├── values-fa_values-fa.arsc.flat
│   │   │   │   │   ├── values-fi_values-fi.arsc.flat
│   │   │   │   │   ├── values-fr-rCA_values-fr-rCA.arsc.flat
│   │   │   │   │   ├── values-fr_values-fr.arsc.flat
│   │   │   │   │   ├── values-gl_values-gl.arsc.flat
│   │   │   │   │   ├── values-gu_values-gu.arsc.flat
│   │   │   │   │   ├── values-hi_values-hi.arsc.flat
│   │   │   │   │   ├── values-hr_values-hr.arsc.flat
│   │   │   │   │   ├── values-hu_values-hu.arsc.flat
│   │   │   │   │   ├── values-hy_values-hy.arsc.flat
│   │   │   │   │   ├── values-in_values-in.arsc.flat
│   │   │   │   │   ├── values-is_values-is.arsc.flat
│   │   │   │   │   ├── values-it_values-it.arsc.flat
│   │   │   │   │   ├── values-iw_values-iw.arsc.flat
│   │   │   │   │   ├── values-ja_values-ja.arsc.flat
│   │   │   │   │   ├── values-ka_values-ka.arsc.flat
│   │   │   │   │   ├── values-kk_values-kk.arsc.flat
│   │   │   │   │   ├── values-km_values-km.arsc.flat
│   │   │   │   │   ├── values-kn_values-kn.arsc.flat
│   │   │   │   │   ├── values-ko_values-ko.arsc.flat
│   │   │   │   │   ├── values-ky_values-ky.arsc.flat
│   │   │   │   │   ├── values-lo_values-lo.arsc.flat
│   │   │   │   │   ├── values-lt_values-lt.arsc.flat
│   │   │   │   │   ├── values-lv_values-lv.arsc.flat
│   │   │   │   │   ├── values-mk_values-mk.arsc.flat
│   │   │   │   │   ├── values-ml_values-ml.arsc.flat
│   │   │   │   │   ├── values-mn_values-mn.arsc.flat
│   │   │   │   │   ├── values-mr_values-mr.arsc.flat
│   │   │   │   │   ├── values-ms_values-ms.arsc.flat
│   │   │   │   │   ├── values-my_values-my.arsc.flat
│   │   │   │   │   ├── values-nb_values-nb.arsc.flat
│   │   │   │   │   ├── values-ne_values-ne.arsc.flat
│   │   │   │   │   ├── values-nl_values-nl.arsc.flat
│   │   │   │   │   ├── values-or_values-or.arsc.flat
│   │   │   │   │   ├── values-pa_values-pa.arsc.flat
│   │   │   │   │   ├── values-pl_values-pl.arsc.flat
│   │   │   │   │   ├── values-pt-rBR_values-pt-rBR.arsc.flat
│   │   │   │   │   ├── values-pt-rPT_values-pt-rPT.arsc.flat
│   │   │   │   │   ├── values-pt_values-pt.arsc.flat
│   │   │   │   │   ├── values-ro_values-ro.arsc.flat
│   │   │   │   │   ├── values-ru_values-ru.arsc.flat
│   │   │   │   │   ├── values-si_values-si.arsc.flat
│   │   │   │   │   ├── values-sk_values-sk.arsc.flat
│   │   │   │   │   ├── values-sl_values-sl.arsc.flat
│   │   │   │   │   ├── values-sq_values-sq.arsc.flat
│   │   │   │   │   ├── values-sr_values-sr.arsc.flat
│   │   │   │   │   ├── values-sv_values-sv.arsc.flat
│   │   │   │   │   ├── values-sw_values-sw.arsc.flat
│   │   │   │   │   ├── values-ta_values-ta.arsc.flat
│   │   │   │   │   ├── values-te_values-te.arsc.flat
│   │   │   │   │   ├── values-th_values-th.arsc.flat
│   │   │   │   │   ├── values-tl_values-tl.arsc.flat
│   │   │   │   │   ├── values-tr_values-tr.arsc.flat
│   │   │   │   │   ├── values-uk_values-uk.arsc.flat
│   │   │   │   │   ├── values-ur_values-ur.arsc.flat
│   │   │   │   │   ├── values-uz_values-uz.arsc.flat
│   │   │   │   │   ├── values-v21_values-v21.arsc.flat
│   │   │   │   │   ├── values-vi_values-vi.arsc.flat
│   │   │   │   │   ├── values-zh-rCN_values-zh-rCN.arsc.flat
│   │   │   │   │   ├── values-zh-rHK_values-zh-rHK.arsc.flat
│   │   │   │   │   ├── values-zh-rTW_values-zh-rTW.arsc.flat
│   │   │   │   │   ├── values-zu_values-zu.arsc.flat
│   │   │   │   │   └── values_values.arsc.flat
│   │   │   │   ├── merged_res_blame_folder/debugAndroidTest/mergeDebugAndroidTestResources/out/multi-v2/
│   │   │   │   │   ├── mergeDebugAndroidTestResources.json
│   │   │   │   │   ├── values-af.json
│   │   │   │   │   ├── values-am.json
│   │   │   │   │   ├── values-ar.json
│   │   │   │   │   ├── values-as.json
│   │   │   │   │   ├── values-az.json
│   │   │   │   │   ├── values-b+sr+Latn.json
│   │   │   │   │   ├── values-be.json
│   │   │   │   │   ├── values-bg.json
│   │   │   │   │   ├── values-bn.json
│   │   │   │   │   ├── values-bs.json
│   │   │   │   │   ├── values-ca.json
│   │   │   │   │   ├── values-cs.json
│   │   │   │   │   ├── values-da.json
│   │   │   │   │   ├── values-de.json
│   │   │   │   │   ├── values-el.json
│   │   │   │   │   ├── values-en-rAU.json
│   │   │   │   │   ├── values-en-rCA.json
│   │   │   │   │   ├── values-en-rGB.json
│   │   │   │   │   ├── values-en-rIN.json
│   │   │   │   │   ├── values-en-rXC.json
│   │   │   │   │   ├── values-es-rUS.json
│   │   │   │   │   ├── values-es.json
│   │   │   │   │   ├── values-et.json
│   │   │   │   │   ├── values-eu.json
│   │   │   │   │   ├── values-fa.json
│   │   │   │   │   ├── values-fi.json
│   │   │   │   │   ├── values-fr-rCA.json
│   │   │   │   │   ├── values-fr.json
│   │   │   │   │   ├── values-gl.json
│   │   │   │   │   ├── values-gu.json
│   │   │   │   │   ├── values-hi.json
│   │   │   │   │   ├── values-hr.json
│   │   │   │   │   ├── values-hu.json
│   │   │   │   │   ├── values-hy.json
│   │   │   │   │   ├── values-in.json
│   │   │   │   │   ├── values-is.json
│   │   │   │   │   ├── values-it.json
│   │   │   │   │   ├── values-iw.json
│   │   │   │   │   ├── values-ja.json
│   │   │   │   │   ├── values-ka.json
│   │   │   │   │   ├── values-kk.json
│   │   │   │   │   ├── values-km.json
│   │   │   │   │   ├── values-kn.json
│   │   │   │   │   ├── values-ko.json
│   │   │   │   │   ├── values-ky.json
│   │   │   │   │   ├── values-lo.json
│   │   │   │   │   ├── values-lt.json
│   │   │   │   │   ├── values-lv.json
│   │   │   │   │   ├── values-mk.json
│   │   │   │   │   ├── values-ml.json
│   │   │   │   │   ├── values-mn.json
│   │   │   │   │   ├── values-mr.json
│   │   │   │   │   ├── values-ms.json
│   │   │   │   │   ├── values-my.json
│   │   │   │   │   ├── values-nb.json
│   │   │   │   │   ├── values-ne.json
│   │   │   │   │   ├── values-nl.json
│   │   │   │   │   ├── values-or.json
│   │   │   │   │   ├── values-pa.json
│   │   │   │   │   ├── values-pl.json
│   │   │   │   │   ├── values-pt-rBR.json
│   │   │   │   │   ├── values-pt-rPT.json
│   │   │   │   │   ├── values-pt.json
│   │   │   │   │   ├── values-ro.json
│   │   │   │   │   ├── values-ru.json
│   │   │   │   │   ├── values-si.json
│   │   │   │   │   ├── values-sk.json
│   │   │   │   │   ├── values-sl.json
│   │   │   │   │   ├── values-sq.json
│   │   │   │   │   ├── values-sr.json
│   │   │   │   │   ├── values-sv.json
│   │   │   │   │   ├── values-sw.json
│   │   │   │   │   ├── values-ta.json
│   │   │   │   │   ├── values-te.json
│   │   │   │   │   ├── values-th.json
│   │   │   │   │   ├── values-tl.json
│   │   │   │   │   ├── values-tr.json
│   │   │   │   │   ├── values-uk.json
│   │   │   │   │   ├── values-ur.json
│   │   │   │   │   ├── values-uz.json
│   │   │   │   │   ├── values-v21.json
│   │   │   │   │   ├── values-vi.json
│   │   │   │   │   ├── values-zh-rCN.json
│   │   │   │   │   ├── values-zh-rHK.json
│   │   │   │   │   ├── values-zh-rTW.json
│   │   │   │   │   ├── values-zu.json
│   │   │   │   │   └── values.json
│   │   │   │   ├── mixed_scope_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── navigation_json/
│   │   │   │   │   ├── debug/extractDeepLinksDebug/
│   │   │   │   │   │   └── navigation.json
│   │   │   │   │   └── release/extractDeepLinksRelease/
│   │   │   │   │       └── navigation.json
│   │   │   │   ├── navigation_json_for_aar/debug/extractDeepLinksForAarDebug/
│   │   │   │   ├── nested_resources_validation_report/
│   │   │   │   │   ├── debug/generateDebugResources/
│   │   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestResources/
│   │   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   │   └── release/generateReleaseResources/
│   │   │   │   │       └── nestedResourcesValidationReport.txt
│   │   │   │   ├── packaged_manifests/debugAndroidTest/processDebugAndroidTestManifest/
│   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── packaged_res/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   ├── project_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   │   ├── 302977b48824c3ed46d909c3f6799c5b115547ef0b31dabfee949e9253adff71_0.jar
│   │   │   │   │   ├── 302977b48824c3ed46d909c3f6799c5b115547ef0b31dabfee949e9253adff71_1.jar
│   │   │   │   │   ├── 302977b48824c3ed46d909c3f6799c5b115547ef0b31dabfee949e9253adff71_2.jar
│   │   │   │   │   ├── 302977b48824c3ed46d909c3f6799c5b115547ef0b31dabfee949e9253adff71_3.jar
│   │   │   │   │   ├── 302977b48824c3ed46d909c3f6799c5b115547ef0b31dabfee949e9253adff71_4.jar
│   │   │   │   │   └── 302977b48824c3ed46d909c3f6799c5b115547ef0b31dabfee949e9253adff71_5.jar
│   │   │   │   ├── public_res/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   ├── runtime_library_classes_dir/debug/bundleLibRuntimeToDirDebug/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/storage/
│   │   │   │   │   │   ├── di/
│   │   │   │   │   │   │   ├── StorageModule.class
│   │   │   │   │   │   │   ├── StorageModule_ProvideAppDatabaseFactory.class
│   │   │   │   │   │   │   └── StorageModule_ProvideSecureItemDaoFactory.class
│   │   │   │   │   │   ├── AppDatabase.class
│   │   │   │   │   │   ├── AppDatabase_Impl$createOpenDelegate$_openDelegate$1.class
│   │   │   │   │   │   ├── AppDatabase_Impl.class
│   │   │   │   │   │   ├── SecureItemDao.class
│   │   │   │   │   │   ├── SecureItemDao_Impl$Companion.class
│   │   │   │   │   │   ├── SecureItemDao_Impl.class
│   │   │   │   │   │   └── SecureItemEntity.class
│   │   │   │   │   ├── hilt_aggregated_deps/
│   │   │   │   │   │   └── _com_miguelrodriguez19_safecube_core_storage_di_StorageModule.class
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── storage.kotlin_module
│   │   │   │   ├── runtime_library_classes_jar/
│   │   │   │   │   ├── debug/bundleLibRuntimeToJarDebug/
│   │   │   │   │   │   └── classes.jar
│   │   │   │   │   └── release/bundleLibRuntimeToJarRelease/
│   │   │   │   │       └── classes.jar
│   │   │   │   ├── runtime_symbol_list/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── R.txt
│   │   │   │   ├── signing_config_versions/debugAndroidTest/writeDebugAndroidTestSigningConfigVersions/
│   │   │   │   │   └── signing-config-versions.json
│   │   │   │   ├── stable_resource_ids_file/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── stableIds.txt
│   │   │   │   ├── sub_project_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── symbol_list_with_package_name/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── package-aware-r.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── package-aware-r.txt
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── package-aware-r.txt
│   │   │   │   ├── tmp/manifest/androidTest/debug/
│   │   │   │   └── validate_signing_config/debugAndroidTest/validateSigningDebugAndroidTest/
│   │   │   ├── kotlin/
│   │   │   │   ├── compileDebugKotlin/
│   │   │   │   │   ├── cacheable/
│   │   │   │   │   │   ├── caches-jvm/
│   │   │   │   │   │   │   ├── compilerPluginFiles/
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │   │   │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │   │   │   │   ├── inputs/
│   │   │   │   │   │   │   │   ├── source-to-output.tab
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.values.at
│   │   │   │   │   │   │   │   ├── source-to-output.tab_i
│   │   │   │   │   │   │   │   └── source-to-output.tab_i.len
│   │   │   │   │   │   │   ├── jvm/
│   │   │   │   │   │   │   │   └── kotlin/
│   │   │   │   │   │   │   │       ├── class-attributes.tab
│   │   │   │   │   │   │   │       ├── class-attributes.tab.keystream
│   │   │   │   │   │   │   │       ├── class-attributes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── class-attributes.tab.len
│   │   │   │   │   │   │   │       ├── class-attributes.tab.values.at
│   │   │   │   │   │   │   │       ├── class-attributes.tab_i
│   │   │   │   │   │   │   │       ├── class-attributes.tab_i.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.len
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab.values.at
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i
│   │   │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │   │   │   │   │       ├── proto.tab
│   │   │   │   │   │   │   │       ├── proto.tab.keystream
│   │   │   │   │   │   │   │       ├── proto.tab.keystream.len
│   │   │   │   │   │   │   │       ├── proto.tab.len
│   │   │   │   │   │   │   │       ├── proto.tab.values.at
│   │   │   │   │   │   │   │       ├── proto.tab_i
│   │   │   │   │   │   │   │       ├── proto.tab_i.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │   │   │   │   │       ├── source-to-classes.tab_i
│   │   │   │   │   │   │   │       ├── source-to-classes.tab_i.len
│   │   │   │   │   │   │   │       ├── subtypes.tab
│   │   │   │   │   │   │   │       ├── subtypes.tab.keystream
│   │   │   │   │   │   │   │       ├── subtypes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── subtypes.tab.len
│   │   │   │   │   │   │   │       ├── subtypes.tab.values.at
│   │   │   │   │   │   │   │       ├── subtypes.tab_i
│   │   │   │   │   │   │   │       ├── subtypes.tab_i.len
│   │   │   │   │   │   │   │       ├── supertypes.tab
│   │   │   │   │   │   │   │       ├── supertypes.tab.keystream
│   │   │   │   │   │   │   │       ├── supertypes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── supertypes.tab.len
│   │   │   │   │   │   │   │       ├── supertypes.tab.values.at
│   │   │   │   │   │   │   │       ├── supertypes.tab_i
│   │   │   │   │   │   │   │       └── supertypes.tab_i.len
│   │   │   │   │   │   │   └── lookups/
│   │   │   │   │   │   │       ├── counters.tab
│   │   │   │   │   │   │       ├── file-to-id.tab
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream.len
│   │   │   │   │   │   │       ├── file-to-id.tab.len
│   │   │   │   │   │   │       ├── file-to-id.tab.values.at
│   │   │   │   │   │   │       ├── file-to-id.tab_i
│   │   │   │   │   │   │       ├── file-to-id.tab_i.len
│   │   │   │   │   │   │       ├── id-to-file.tab
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream.len
│   │   │   │   │   │   │       ├── id-to-file.tab.len
│   │   │   │   │   │   │       ├── id-to-file.tab.values.at
│   │   │   │   │   │   │       ├── id-to-file.tab_i
│   │   │   │   │   │   │       ├── id-to-file.tab_i.len
│   │   │   │   │   │   │       ├── lookups.tab
│   │   │   │   │   │   │       ├── lookups.tab.keystream
│   │   │   │   │   │   │       ├── lookups.tab.keystream.len
│   │   │   │   │   │   │       ├── lookups.tab.len
│   │   │   │   │   │   │       ├── lookups.tab.values.at
│   │   │   │   │   │   │       ├── lookups.tab_i
│   │   │   │   │   │   │       └── lookups.tab_i.len
│   │   │   │   │   │   └── last-build.bin
│   │   │   │   │   ├── classpath-snapshot/
│   │   │   │   │   │   └── shrunk-classpath-snapshot.bin
│   │   │   │   │   └── local-state/
│   │   │   │   └── compileReleaseKotlin/
│   │   │   │       ├── cacheable/
│   │   │   │       │   ├── caches-jvm/
│   │   │   │       │   │   ├── compilerPluginFiles/
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │       │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │       │   │   ├── inputs/
│   │   │   │       │   │   │   ├── source-to-output.tab
│   │   │   │       │   │   │   ├── source-to-output.tab.keystream
│   │   │   │       │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │       │   │   │   ├── source-to-output.tab.len
│   │   │   │       │   │   │   ├── source-to-output.tab.values.at
│   │   │   │       │   │   │   ├── source-to-output.tab_i
│   │   │   │       │   │   │   └── source-to-output.tab_i.len
│   │   │   │       │   │   ├── jvm/
│   │   │   │       │   │   │   └── kotlin/
│   │   │   │       │   │   │       ├── class-attributes.tab
│   │   │   │       │   │   │       ├── class-attributes.tab.keystream
│   │   │   │       │   │   │       ├── class-attributes.tab.keystream.len
│   │   │   │       │   │   │       ├── class-attributes.tab.len
│   │   │   │       │   │   │       ├── class-attributes.tab.values.at
│   │   │   │       │   │   │       ├── class-attributes.tab_i
│   │   │   │       │   │   │       ├── class-attributes.tab_i.len
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.len
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab.values.at
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab_i
│   │   │   │       │   │   │       ├── class-fq-name-to-source.tab_i.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │       │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │       │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │       │   │   │       ├── proto.tab
│   │   │   │       │   │   │       ├── proto.tab.keystream
│   │   │   │       │   │   │       ├── proto.tab.keystream.len
│   │   │   │       │   │   │       ├── proto.tab.len
│   │   │   │       │   │   │       ├── proto.tab.values.at
│   │   │   │       │   │   │       ├── proto.tab_i
│   │   │   │       │   │   │       ├── proto.tab_i.len
│   │   │   │       │   │   │       ├── source-to-classes.tab
│   │   │   │       │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │       │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │       │   │   │       ├── source-to-classes.tab.len
│   │   │   │       │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │       │   │   │       ├── source-to-classes.tab_i
│   │   │   │       │   │   │       ├── source-to-classes.tab_i.len
│   │   │   │       │   │   │       ├── subtypes.tab
│   │   │   │       │   │   │       ├── subtypes.tab.keystream
│   │   │   │       │   │   │       ├── subtypes.tab.keystream.len
│   │   │   │       │   │   │       ├── subtypes.tab.len
│   │   │   │       │   │   │       ├── subtypes.tab.values.at
│   │   │   │       │   │   │       ├── subtypes.tab_i
│   │   │   │       │   │   │       ├── subtypes.tab_i.len
│   │   │   │       │   │   │       ├── supertypes.tab
│   │   │   │       │   │   │       ├── supertypes.tab.keystream
│   │   │   │       │   │   │       ├── supertypes.tab.keystream.len
│   │   │   │       │   │   │       ├── supertypes.tab.len
│   │   │   │       │   │   │       ├── supertypes.tab.values.at
│   │   │   │       │   │   │       ├── supertypes.tab_i
│   │   │   │       │   │   │       └── supertypes.tab_i.len
│   │   │   │       │   │   └── lookups/
│   │   │   │       │   │       ├── counters.tab
│   │   │   │       │   │       ├── file-to-id.tab
│   │   │   │       │   │       ├── file-to-id.tab.keystream
│   │   │   │       │   │       ├── file-to-id.tab.keystream.len
│   │   │   │       │   │       ├── file-to-id.tab.len
│   │   │   │       │   │       ├── file-to-id.tab.values.at
│   │   │   │       │   │       ├── file-to-id.tab_i
│   │   │   │       │   │       ├── file-to-id.tab_i.len
│   │   │   │       │   │       ├── id-to-file.tab
│   │   │   │       │   │       ├── id-to-file.tab.keystream
│   │   │   │       │   │       ├── id-to-file.tab.keystream.len
│   │   │   │       │   │       ├── id-to-file.tab.len
│   │   │   │       │   │       ├── id-to-file.tab.values.at
│   │   │   │       │   │       ├── id-to-file.tab_i
│   │   │   │       │   │       ├── id-to-file.tab_i.len
│   │   │   │       │   │       ├── lookups.tab
│   │   │   │       │   │       ├── lookups.tab.keystream
│   │   │   │       │   │       ├── lookups.tab.keystream.len
│   │   │   │       │   │       ├── lookups.tab.len
│   │   │   │       │   │       ├── lookups.tab.values.at
│   │   │   │       │   │       ├── lookups.tab_i
│   │   │   │       │   │       └── lookups.tab_i.len
│   │   │   │       │   └── last-build.bin
│   │   │   │       ├── classpath-snapshot/
│   │   │   │       │   └── shrunk-classpath-snapshot.bin
│   │   │   │       └── local-state/
│   │   │   ├── kspCaches/
│   │   │   │   ├── debug/
│   │   │   │   │   ├── backups/
│   │   │   │   │   │   ├── java/
│   │   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/core/storage/di/
│   │   │   │   │   │   │   │   ├── StorageModule_ProvideAppDatabaseFactory.java
│   │   │   │   │   │   │   │   └── StorageModule_ProvideSecureItemDaoFactory.java
│   │   │   │   │   │   │   └── hilt_aggregated_deps/
│   │   │   │   │   │   │       └── _com_miguelrodriguez19_safecube_core_storage_di_StorageModule.java
│   │   │   │   │   │   └── kotlin/com/miguelrodriguez19/safecube/core/storage/
│   │   │   │   │   │       ├── AppDatabase_Impl.kt
│   │   │   │   │   │       └── SecureItemDao_Impl.kt
│   │   │   │   │   ├── logs/
│   │   │   │   │   ├── symbolLookups/
│   │   │   │   │   │   ├── counters.tab
│   │   │   │   │   │   ├── file-to-id.tab
│   │   │   │   │   │   ├── file-to-id.tab.keystream
│   │   │   │   │   │   ├── file-to-id.tab.keystream.len
│   │   │   │   │   │   ├── file-to-id.tab.len
│   │   │   │   │   │   ├── file-to-id.tab.values.at
│   │   │   │   │   │   ├── file-to-id.tab_i
│   │   │   │   │   │   ├── file-to-id.tab_i.len
│   │   │   │   │   │   ├── id-to-file.tab
│   │   │   │   │   │   ├── id-to-file.tab.keystream
│   │   │   │   │   │   ├── id-to-file.tab.keystream.len
│   │   │   │   │   │   ├── id-to-file.tab.len
│   │   │   │   │   │   ├── id-to-file.tab.values.at
│   │   │   │   │   │   ├── id-to-file.tab_i
│   │   │   │   │   │   ├── id-to-file.tab_i.len
│   │   │   │   │   │   ├── lookups.tab
│   │   │   │   │   │   ├── lookups.tab.keystream
│   │   │   │   │   │   ├── lookups.tab.keystream.len
│   │   │   │   │   │   ├── lookups.tab.len
│   │   │   │   │   │   ├── lookups.tab.values.at
│   │   │   │   │   │   ├── lookups.tab_i
│   │   │   │   │   │   └── lookups.tab_i.len
│   │   │   │   │   ├── ap-classpath-entries.bin
│   │   │   │   │   ├── caches.uptodate
│   │   │   │   │   ├── classpath-entries.bin
│   │   │   │   │   ├── classpath-structure.bin
│   │   │   │   │   ├── sealed
│   │   │   │   │   ├── sourceToOutputs
│   │   │   │   │   └── symbols
│   │   │   │   └── release/
│   │   │   │       ├── backups/
│   │   │   │       │   ├── java/
│   │   │   │       │   │   ├── com/miguelrodriguez19/safecube/core/storage/di/
│   │   │   │       │   │   │   ├── StorageModule_ProvideAppDatabaseFactory.java
│   │   │   │       │   │   │   └── StorageModule_ProvideSecureItemDaoFactory.java
│   │   │   │       │   │   └── hilt_aggregated_deps/
│   │   │   │       │   │       └── _com_miguelrodriguez19_safecube_core_storage_di_StorageModule.java
│   │   │   │       │   └── kotlin/com/miguelrodriguez19/safecube/core/storage/
│   │   │   │       │       ├── AppDatabase_Impl.kt
│   │   │   │       │       └── SecureItemDao_Impl.kt
│   │   │   │       ├── logs/
│   │   │   │       ├── symbolLookups/
│   │   │   │       │   ├── counters.tab
│   │   │   │       │   ├── file-to-id.tab
│   │   │   │       │   ├── file-to-id.tab.keystream
│   │   │   │       │   ├── file-to-id.tab.keystream.len
│   │   │   │       │   ├── file-to-id.tab.len
│   │   │   │       │   ├── file-to-id.tab.values.at
│   │   │   │       │   ├── file-to-id.tab_i
│   │   │   │       │   ├── file-to-id.tab_i.len
│   │   │   │       │   ├── id-to-file.tab
│   │   │   │       │   ├── id-to-file.tab.keystream
│   │   │   │       │   ├── id-to-file.tab.keystream.len
│   │   │   │       │   ├── id-to-file.tab.len
│   │   │   │       │   ├── id-to-file.tab.values.at
│   │   │   │       │   ├── id-to-file.tab_i
│   │   │   │       │   ├── id-to-file.tab_i.len
│   │   │   │       │   ├── lookups.tab
│   │   │   │       │   ├── lookups.tab.keystream
│   │   │   │       │   ├── lookups.tab.keystream.len
│   │   │   │       │   ├── lookups.tab.len
│   │   │   │       │   ├── lookups.tab.values.at
│   │   │   │       │   ├── lookups.tab_i
│   │   │   │       │   └── lookups.tab_i.len
│   │   │   │       ├── ap-classpath-entries.bin
│   │   │   │       ├── caches.uptodate
│   │   │   │       ├── classpath-entries.bin
│   │   │   │       ├── classpath-structure.bin
│   │   │   │       ├── sealed
│   │   │   │       ├── sourceToOutputs
│   │   │   │       └── symbols
│   │   │   ├── outputs/
│   │   │   │   ├── aar/
│   │   │   │   │   └── storage-debug.aar
│   │   │   │   ├── androidTest-results/connected/debug/
│   │   │   │   ├── apk/androidTest/debug/
│   │   │   │   │   ├── output-metadata.json
│   │   │   │   │   └── storage-debug-androidTest.apk
│   │   │   │   ├── code_coverage/debugAndroidTest/connected/
│   │   │   │   │   └── coverage.ec
│   │   │   │   ├── connected_android_test_additional_output/debugAndroidTest/connected/
│   │   │   │   └── logs/
│   │   │   │       ├── manifest-merger-debug-report.txt
│   │   │   │       └── manifest-merger-release-report.txt
│   │   │   ├── reports/androidTests/connected/debug/
│   │   │   │   ├── css/
│   │   │   │   │   ├── base-style.css
│   │   │   │   │   └── style.css
│   │   │   │   ├── js/
│   │   │   │   │   └── report.js
│   │   │   │   └── index.html
│   │   │   └── tmp/
│   │   │       ├── compileDebugJavaWithJavac/
│   │   │       │   ├── compileTransaction/
│   │   │       │   │   ├── backup-dir/
│   │   │       │   │   └── stash-dir/
│   │   │       │   └── previous-compilation-data.bin
│   │   │       └── compileReleaseJavaWithJavac/
│   │   │           └── previous-compilation-data.bin
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/core/storage/
│   │   │   │   ├── di/
│   │   │   │   │   └── StorageModule.kt
│   │   │   │   ├── AppDatabase.kt
│   │   │   │   ├── SecureItemDao.kt
│   │   │   │   └── SecureItemEntity.kt
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── ui/
│   │   ├── build/
│   │   │   ├── .transforms/
│   │   │   │   ├── 250121fd15c61578b250cd250228c1cc/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 25bebab375af5160b45eba51c035a14d/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 2b90f344ab43d2901c24db1a7f3ac265/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 5b7a4232e774954d3ac75c931833d44e/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 7fb47383650d6f51be684b2873b5c6a8/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── a271c624e85a988f8b3cd9706f3650e6/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── d2b08568d2e0c63847ebd148c9660cd8/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   └── f6f9026ef96308f732b33852b4e8c605/
│   │   │   │       ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │       │   └── desugar_graph.bin
│   │   │   │       └── results.bin
│   │   │   ├── generated/
│   │   │   │   ├── res/pngs/
│   │   │   │   │   ├── debug/
│   │   │   │   │   └── release/
│   │   │   │   └── updated_navigation_xml/
│   │   │   │       ├── debug/
│   │   │   │       └── release/
│   │   │   ├── intermediates/
│   │   │   │   ├── aapt_friendly_merged_manifests/
│   │   │   │   │   ├── debug/processDebugManifest/aapt/
│   │   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   │   └── output-metadata.json
│   │   │   │   │   └── release/processReleaseManifest/aapt/
│   │   │   │   │       ├── AndroidManifest.xml
│   │   │   │   │       └── output-metadata.json
│   │   │   │   ├── aar_libs_directory/debug/syncDebugLibJars/libs/
│   │   │   │   ├── aar_main_jar/debug/syncDebugLibJars/
│   │   │   │   │   └── classes.jar
│   │   │   │   ├── aar_metadata/
│   │   │   │   │   ├── debug/writeDebugAarMetadata/
│   │   │   │   │   │   │   │   │   │   │   └── release/writeReleaseAarMetadata/
│   │   │   │   │       │   │   │   │   ├── aar_metadata_check/debug/checkDebugAarMetadata/
│   │   │   │   ├── annotation_processor_list/
│   │   │   │   │   ├── debug/javaPreCompileDebug/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   └── release/javaPreCompileRelease/
│   │   │   │   │       └── annotationProcessors.json
│   │   │   │   ├── annotations_typedef_file/debug/extractDebugAnnotations/
│   │   │   │   │   └── typedefs.txt
│   │   │   │   ├── annotations_zip/debug/extractDebugAnnotations/
│   │   │   │   ├── assets/
│   │   │   │   │   ├── debug/mergeDebugAssets/
│   │   │   │   │   └── release/mergeReleaseAssets/
│   │   │   │   ├── compile_library_classes_jar/
│   │   │   │   │   ├── debug/bundleLibCompileToJarDebug/
│   │   │   │   │   │   └── classes.jar
│   │   │   │   │   └── release/bundleLibCompileToJarRelease/
│   │   │   │   │       └── classes.jar
│   │   │   │   ├── compile_r_class_jar/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── R.jar
│   │   │   │   ├── compile_symbol_list/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── R.txt
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── R.txt
│   │   │   │   ├── compiled_local_resources/debug/compileDebugLibraryResources/out/
│   │   │   │   ├── consumer_proguard_dir/release/
│   │   │   │   ├── data_binding_layout_info_type_package/
│   │   │   │   │   ├── debug/packageDebugResources/out/
│   │   │   │   │   └── release/packageReleaseResources/out/
│   │   │   │   ├── generated_proguard_file/
│   │   │   │   │   ├── debug/mergeDebugGeneratedProguardFiles/
│   │   │   │   │   └── release/mergeReleaseGeneratedProguardFiles/
│   │   │   │   ├── incremental/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   │   ├── values/
│   │   │   │   │   │   │   │   └── values.xml
│   │   │   │   │   │   │   └── values-es/
│   │   │   │   │   │   │       └── values-es.xml
│   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── debug-mergeJavaRes/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   └── merge-state
│   │   │   │   │   ├── mergeDebugAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeReleaseAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeReleaseJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   │       ├── merged.dir/
│   │   │   │   │       │   ├── values/
│   │   │   │   │       │   │   └── values.xml
│   │   │   │   │       │   └── values-es/
│   │   │   │   │       │       └── values-es.xml
│   │   │   │   │       ├── stripped.dir/
│   │   │   │   │       │   │   │   │   │       └── merger.xml
│   │   │   │   ├── library_and_local_jars_jni/debug/copyDebugJniLibsProjectAndLocalJars/jni/
│   │   │   │   ├── library_art_profile/
│   │   │   │   │   ├── debug/prepareDebugArtProfile/
│   │   │   │   │   └── release/prepareReleaseArtProfile/
│   │   │   │   ├── library_jni/
│   │   │   │   │   ├── debug/copyDebugJniLibsProjectOnly/jni/
│   │   │   │   │   └── release/copyReleaseJniLibsProjectOnly/jni/
│   │   │   │   ├── lint_publish_jar/global/prepareLintJarForPublish/
│   │   │   │   ├── local_only_symbol_list/
│   │   │   │   │   ├── debug/parseDebugLocalResources/
│   │   │   │   │   │   └── R-def.txt
│   │   │   │   │   └── release/parseReleaseLocalResources/
│   │   │   │   │       └── R-def.txt
│   │   │   │   ├── manifest_merge_blame_file/
│   │   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   │   └── manifest-merger-blame-debug-report.txt
│   │   │   │   │   └── release/processReleaseManifest/
│   │   │   │   │       └── manifest-merger-blame-release-report.txt
│   │   │   │   ├── merged_consumer_proguard_file/debug/mergeDebugConsumerProguardFiles/
│   │   │   │   ├── merged_java_res/debug/mergeDebugJavaResource/
│   │   │   │   │   └── feature-ui.jar
│   │   │   │   ├── merged_jni_libs/
│   │   │   │   │   ├── debug/mergeDebugJniLibFolders/out/
│   │   │   │   │   └── release/mergeReleaseJniLibFolders/out/
│   │   │   │   ├── merged_manifest/
│   │   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   │   └── AndroidManifest.xml
│   │   │   │   │   └── release/processReleaseManifest/
│   │   │   │   │       └── AndroidManifest.xml
│   │   │   │   ├── navigation_json/
│   │   │   │   │   ├── debug/extractDeepLinksDebug/
│   │   │   │   │   │   └── navigation.json
│   │   │   │   │   └── release/extractDeepLinksRelease/
│   │   │   │   │       └── navigation.json
│   │   │   │   ├── navigation_json_for_aar/debug/extractDeepLinksForAarDebug/
│   │   │   │   ├── nested_resources_validation_report/
│   │   │   │   │   ├── debug/generateDebugResources/
│   │   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   │   └── release/generateReleaseResources/
│   │   │   │   │       └── nestedResourcesValidationReport.txt
│   │   │   │   ├── packaged_res/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   │   ├── values/
│   │   │   │   │   │   │   └── values.xml
│   │   │   │   │   │   └── values-es/
│   │   │   │   │   │       └── values-es.xml
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   │       ├── values/
│   │   │   │   │       │   └── values.xml
│   │   │   │   │       └── values-es/
│   │   │   │   │           └── values-es.xml
│   │   │   │   ├── public_res/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   ├── runtime_library_classes_dir/debug/bundleLibRuntimeToDirDebug/
│   │   │   │   ├── runtime_library_classes_jar/
│   │   │   │   │   ├── debug/bundleLibRuntimeToJarDebug/
│   │   │   │   │   │   └── classes.jar
│   │   │   │   │   └── release/bundleLibRuntimeToJarRelease/
│   │   │   │   │       └── classes.jar
│   │   │   │   └── symbol_list_with_package_name/
│   │   │   │       ├── debug/generateDebugRFile/
│   │   │   │       │   └── package-aware-r.txt
│   │   │   │       └── release/generateReleaseRFile/
│   │   │   │           └── package-aware-r.txt
│   │   │   └── outputs/
│   │   │       ├── aar/
│   │   │       │   └── ui-debug.aar
│   │   │       └── logs/
│   │   │           ├── manifest-merger-debug-report.txt
│   │   │           └── manifest-merger-release-report.txt
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/core/ui/
│   │   │   │   ├── components/
│   │   │   │   │   └── .gitkeep
│   │   │   │   └── theme/
│   │   │   │       └── .gitkeep
│   │   │   ├── res/
│   │   │   │   ├── values/
│   │   │   │   │   └── strings.xml
│   │   │   │   └── values-es/
│   │   │   │       └── strings.xml
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── docs/
│   ├── architecture/
│   │   ├── openapi-auth-contract-integration.md
│   │   └── storage_decision.md
│   ├── package-structure/
│   │   └── package_structure.md
│   ├── roadmap/
│   │   ├── roadmap--fase-1.md
│   │   ├── roadmap--fase-2.md
│   │   └── roadmap--high-level.md
│   ├── README.md
│   └── testing.md
├── feature/
│   ├── auth/
│   │   ├── build/
│   │   │   ├── .transforms/
│   │   │   │   ├── 07e5110335f80363af191bcce2d433cb/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   │   │   │   └── AuthActionLabelKt.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 0eb1334035d3ebed89cafaadd0a0759e/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   │   │   │   ├── navigation/
│   │   │   │   │   │   │   ├── screens/
│   │   │   │   │   │   │   │   ├── ComposableSingletons$LoginScreenKt.dex
│   │   │   │   │   │   │   │   ├── ComposableSingletons$SignupScreenKt.dex
│   │   │   │   │   │   │   │   ├── ComposableSingletons$WelcomeScreenKt.dex
│   │   │   │   │   │   │   │   ├── LoginScreenKt.dex
│   │   │   │   │   │   │   │   ├── PostLoginGateScreenKt$PostLoginGatePlaceholderScreen$1$1.dex
│   │   │   │   │   │   │   │   ├── PostLoginGateScreenKt.dex
│   │   │   │   │   │   │   │   ├── SignupScreenKt.dex
│   │   │   │   │   │   │   │   └── WelcomeScreenKt.dex
│   │   │   │   │   │   │   └── AuthActionLabelKt.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 159eb3405ab740720324cb4ef9a87030/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 21337385c7c979c420b8b5e40dd8167e/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   │   │   │   ├── screens/
│   │   │   │   │   │   │   │   ├── ComposableSingletons$LoginScreenKt.dex
│   │   │   │   │   │   │   │   ├── ComposableSingletons$SignupScreenKt.dex
│   │   │   │   │   │   │   │   ├── ComposableSingletons$WelcomeScreenKt.dex
│   │   │   │   │   │   │   │   ├── LoginScreenKt.dex
│   │   │   │   │   │   │   │   ├── PostLoginGateScreenKt$PostLoginGateScreen$1$1.dex
│   │   │   │   │   │   │   │   ├── PostLoginGateScreenKt.dex
│   │   │   │   │   │   │   │   ├── SignupScreenKt.dex
│   │   │   │   │   │   │   │   └── WelcomeScreenKt.dex
│   │   │   │   │   │   │   └── AuthActionLabelKt.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 49bb41a09d911414d05ebac5fa262f28/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   │   │   │   ├── screens/
│   │   │   │   │   │   │   │   ├── ComposableSingletons$LoginScreenKt.dex
│   │   │   │   │   │   │   │   ├── ComposableSingletons$SignupScreenKt.dex
│   │   │   │   │   │   │   │   ├── ComposableSingletons$WelcomeScreenKt.dex
│   │   │   │   │   │   │   │   ├── LoginScreenKt.dex
│   │   │   │   │   │   │   │   ├── PostLoginGateScreenKt.dex
│   │   │   │   │   │   │   │   ├── SignupScreenKt.dex
│   │   │   │   │   │   │   │   └── WelcomeScreenKt.dex
│   │   │   │   │   │   │   └── AuthActionLabelKt.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 54fd443ee1d0fecdeca973703b6e7ca0/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 61291fd663c4b6e142a5c0eb70ca459a/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   │   │   │   └── AuthActionLabelKt.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 6bf6957f22c77d21eb99fb1815a87a84/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   │   │   │   ├── screens/
│   │   │   │   │   │   │   │   ├── ComposableSingletons$LoginScreenKt.dex
│   │   │   │   │   │   │   │   ├── ComposableSingletons$SignupScreenKt.dex
│   │   │   │   │   │   │   │   ├── ComposableSingletons$WelcomeScreenKt.dex
│   │   │   │   │   │   │   │   ├── LoginScreenKt.dex
│   │   │   │   │   │   │   │   ├── PostLoginGateScreenKt.dex
│   │   │   │   │   │   │   │   ├── SignupScreenKt.dex
│   │   │   │   │   │   │   │   └── WelcomeScreenKt.dex
│   │   │   │   │   │   │   └── AuthActionLabelKt.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 85a8e21beffa687d21ee90944afe4523/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   │   │   │   ├── screens/
│   │   │   │   │   │   │   │   ├── ComposableSingletons$LoginScreenKt.dex
│   │   │   │   │   │   │   │   ├── ComposableSingletons$SignupScreenKt.dex
│   │   │   │   │   │   │   │   ├── ComposableSingletons$WelcomeScreenKt.dex
│   │   │   │   │   │   │   │   ├── LoginScreenKt.dex
│   │   │   │   │   │   │   │   ├── PostLoginGateScreenKt$PostLoginGateScreen$1$1.dex
│   │   │   │   │   │   │   │   ├── PostLoginGateScreenKt.dex
│   │   │   │   │   │   │   │   ├── SignupScreenKt.dex
│   │   │   │   │   │   │   │   └── WelcomeScreenKt.dex
│   │   │   │   │   │   │   └── AuthActionLabelKt.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 9be2ca84e046104ac13d24383f836449/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── daa3276f273338cde9de5bf4def18d11/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   └── e6b26fab5b14c1ecbe9816fb032887bb/
│   │   │   │       ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │       │   ├── com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │       │   │   ├── navigation/
│   │   │   │       │   │   │   ├── AuthPlaceholderScreensKt.dex
│   │   │   │       │   │   │   └── ComposableSingletons$AuthPlaceholderScreensKt.dex
│   │   │   │       │   │   └── AuthActionLabelKt.dex
│   │   │   │       │   └── desugar_graph.bin
│   │   │   │       └── results.bin
│   │   │   ├── generated/
│   │   │   │   ├── res/pngs/
│   │   │   │   │   ├── debug/
│   │   │   │   │   └── release/
│   │   │   │   └── updated_navigation_xml/
│   │   │   │       ├── debug/
│   │   │   │       ├── debugAndroidTest/
│   │   │   │       └── release/
│   │   │   ├── intermediates/
│   │   │   │   ├── aapt_friendly_merged_manifests/
│   │   │   │   │   ├── debug/processDebugManifest/aapt/
│   │   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   │   └── output-metadata.json
│   │   │   │   │   └── release/processReleaseManifest/aapt/
│   │   │   │   │       ├── AndroidManifest.xml
│   │   │   │   │       └── output-metadata.json
│   │   │   │   ├── aar_libs_directory/debug/syncDebugLibJars/libs/
│   │   │   │   ├── aar_main_jar/debug/syncDebugLibJars/
│   │   │   │   │   └── classes.jar
│   │   │   │   ├── aar_metadata/
│   │   │   │   │   ├── debug/writeDebugAarMetadata/
│   │   │   │   │   │   │   │   │   │   │   └── release/writeReleaseAarMetadata/
│   │   │   │   │       │   │   │   │   ├── aar_metadata_check/
│   │   │   │   │   ├── debug/checkDebugAarMetadata/
│   │   │   │   │   └── debugAndroidTest/checkDebugAndroidTestAarMetadata/
│   │   │   │   ├── android_res_source_set_path_map/debugAndroidTest/mapDebugAndroidTestSourceSetPaths/
│   │   │   │   │   └── file-map.txt
│   │   │   │   ├── annotation_processor_list/
│   │   │   │   │   ├── debug/javaPreCompileDebug/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   ├── debugAndroidTest/javaPreCompileDebugAndroidTest/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   ├── debugUnitTest/javaPreCompileDebugUnitTest/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   └── release/javaPreCompileRelease/
│   │   │   │   │       └── annotationProcessors.json
│   │   │   │   ├── annotations_typedef_file/debug/extractDebugAnnotations/
│   │   │   │   │   └── typedefs.txt
│   │   │   │   ├── annotations_zip/debug/extractDebugAnnotations/
│   │   │   │   ├── apk_ide_redirect_file/debugAndroidTest/createDebugAndroidTestApkListingFileRedirect/
│   │   │   │   │   └── redirect.txt
│   │   │   │   ├── assets/
│   │   │   │   │   ├── debug/mergeDebugAssets/
│   │   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestAssets/
│   │   │   │   │   │   └── PublicSuffixDatabase.list
│   │   │   │   │   └── release/mergeReleaseAssets/
│   │   │   │   ├── built_in_kotlinc/
│   │   │   │   │   ├── debug/compileDebugKotlin/classes/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   │   │   │   ├── screens/
│   │   │   │   │   │   │   │   ├── ComposableSingletons$LoginScreenKt.class
│   │   │   │   │   │   │   │   ├── ComposableSingletons$SignupScreenKt.class
│   │   │   │   │   │   │   │   ├── ComposableSingletons$WelcomeScreenKt.class
│   │   │   │   │   │   │   │   ├── LoginScreenKt.class
│   │   │   │   │   │   │   │   ├── PostLoginGateScreenKt.class
│   │   │   │   │   │   │   │   ├── SignupScreenKt.class
│   │   │   │   │   │   │   │   └── WelcomeScreenKt.class
│   │   │   │   │   │   │   └── AuthActionLabelKt.class
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── auth.kotlin_module
│   │   │   │   │   └── release/compileReleaseKotlin/classes/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   │       │   ├── screens/
│   │   │   │   │       │   │   ├── ComposableSingletons$LoginScreenKt.class
│   │   │   │   │       │   │   ├── ComposableSingletons$SignupScreenKt.class
│   │   │   │   │       │   │   ├── ComposableSingletons$WelcomeScreenKt.class
│   │   │   │   │       │   │   ├── LoginScreenKt.class
│   │   │   │   │       │   │   ├── PostLoginGateScreenKt.class
│   │   │   │   │       │   │   ├── SignupScreenKt.class
│   │   │   │   │       │   │   └── WelcomeScreenKt.class
│   │   │   │   │       │   └── AuthActionLabelKt.class
│   │   │   │   │       └── META-INF/
│   │   │   │   │           └── auth.kotlin_module
│   │   │   │   ├── compile_and_runtime_r_class_jar/
│   │   │   │   │   ├── debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   └── debugUnitTest/generateDebugUnitTestStubRFile/
│   │   │   │   │       └── R.jar
│   │   │   │   ├── compile_library_classes_jar/
│   │   │   │   │   ├── debug/bundleLibCompileToJarDebug/
│   │   │   │   │   │   └── classes.jar
│   │   │   │   │   └── release/bundleLibCompileToJarRelease/
│   │   │   │   │       └── classes.jar
│   │   │   │   ├── compile_r_class_jar/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── R.jar
│   │   │   │   ├── compile_symbol_list/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── R.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── R.txt
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── R.txt
│   │   │   │   ├── compiled_local_resources/debug/compileDebugLibraryResources/out/
│   │   │   │   ├── compiled_navigation_res/debugAndroidTest/compileDebugAndroidTestNavigationResources/
│   │   │   │   ├── compressed_assets/debugAndroidTest/compressDebugAndroidTestAssets/out/assets/
│   │   │   │   │   └── PublicSuffixDatabase.list.jar
│   │   │   │   ├── consumer_proguard_dir/release/
│   │   │   │   ├── data_binding_layout_info_type_merge/debugAndroidTest/mergeDebugAndroidTestResources/out/
│   │   │   │   ├── data_binding_layout_info_type_package/
│   │   │   │   │   ├── debug/packageDebugResources/out/
│   │   │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/out/
│   │   │   │   │   └── release/packageReleaseResources/out/
│   │   │   │   ├── desugar_graph/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   │   ├── currentProject/
│   │   │   │   │   │   ├── jar_ae248e1c7db4af288115b9299d08b379186265b9770b20eff1c56c87c42bf4c0_bucket_0/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_ae248e1c7db4af288115b9299d08b379186265b9770b20eff1c56c87c42bf4c0_bucket_1/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_ae248e1c7db4af288115b9299d08b379186265b9770b20eff1c56c87c42bf4c0_bucket_2/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_ae248e1c7db4af288115b9299d08b379186265b9770b20eff1c56c87c42bf4c0_bucket_3/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_ae248e1c7db4af288115b9299d08b379186265b9770b20eff1c56c87c42bf4c0_bucket_4/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   └── jar_ae248e1c7db4af288115b9299d08b379186265b9770b20eff1c56c87c42bf4c0_bucket_5/
│   │   │   │   │   │       └── graph.bin
│   │   │   │   │   ├── externalLibs/
│   │   │   │   │   ├── mixedScopes/
│   │   │   │   │   └── otherProjects/
│   │   │   │   ├── dex/debugAndroidTest/
│   │   │   │   │   ├── mergeExtDexDebugAndroidTest/
│   │   │   │   │   │   └── classes.dex
│   │   │   │   │   ├── mergeLibDexDebugAndroidTest/
│   │   │   │   │   │   ├── 0/
│   │   │   │   │   │   ├── 1/
│   │   │   │   │   │   ├── 10/
│   │   │   │   │   │   ├── 11/
│   │   │   │   │   │   ├── 12/
│   │   │   │   │   │   ├── 13/
│   │   │   │   │   │   ├── 14/
│   │   │   │   │   │   ├── 15/
│   │   │   │   │   │   ├── 2/
│   │   │   │   │   │   ├── 3/
│   │   │   │   │   │   ├── 4/
│   │   │   │   │   │   ├── 5/
│   │   │   │   │   │   ├── 6/
│   │   │   │   │   │   ├── 7/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 8/
│   │   │   │   │   │   └── 9/
│   │   │   │   │   └── mergeProjectDexDebugAndroidTest/
│   │   │   │   │       ├── 0/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 1/
│   │   │   │   │       ├── 10/
│   │   │   │   │       ├── 11/
│   │   │   │   │       ├── 12/
│   │   │   │   │       ├── 13/
│   │   │   │   │       ├── 14/
│   │   │   │   │       ├── 15/
│   │   │   │   │       ├── 2/
│   │   │   │   │       ├── 3/
│   │   │   │   │       ├── 4/
│   │   │   │   │       ├── 5/
│   │   │   │   │       ├── 6/
│   │   │   │   │       ├── 7/
│   │   │   │   │       ├── 8/
│   │   │   │   │       └── 9/
│   │   │   │   ├── dex_archive_input_jar_hashes/debugAndroidTest/dexBuilderDebugAndroidTest/
│   │   │   │   │   └── out
│   │   │   │   ├── dex_number_of_buckets_file/debugAndroidTest/dexBuilderDebugAndroidTest/
│   │   │   │   │   └── out
│   │   │   │   ├── duplicate_classes_check/debugAndroidTest/checkDebugAndroidTestDuplicateClasses/
│   │   │   │   ├── external_file_lib_dex_archives/debugAndroidTest/desugarDebugAndroidTestFileDependencies/
│   │   │   │   ├── external_libs_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── external_libs_dex_archive_with_artifact_transforms/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── generated_proguard_file/
│   │   │   │   │   ├── debug/mergeDebugGeneratedProguardFiles/
│   │   │   │   │   └── release/mergeReleaseGeneratedProguardFiles/
│   │   │   │   ├── incremental/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── debug-mergeJavaRes/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   └── merge-state
│   │   │   │   │   ├── debugAndroidTest/
│   │   │   │   │   │   ├── mergeDebugAndroidTestResources/
│   │   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   │   │   ├── values/
│   │   │   │   │   │   │   │   │   └── values.xml
│   │   │   │   │   │   │   │   ├── values-af/
│   │   │   │   │   │   │   │   │   └── values-af.xml
│   │   │   │   │   │   │   │   ├── values-am/
│   │   │   │   │   │   │   │   │   └── values-am.xml
│   │   │   │   │   │   │   │   ├── values-ar/
│   │   │   │   │   │   │   │   │   └── values-ar.xml
│   │   │   │   │   │   │   │   ├── values-as/
│   │   │   │   │   │   │   │   │   └── values-as.xml
│   │   │   │   │   │   │   │   ├── values-az/
│   │   │   │   │   │   │   │   │   └── values-az.xml
│   │   │   │   │   │   │   │   ├── values-b+sr+Latn/
│   │   │   │   │   │   │   │   │   └── values-b+sr+Latn.xml
│   │   │   │   │   │   │   │   ├── values-be/
│   │   │   │   │   │   │   │   │   └── values-be.xml
│   │   │   │   │   │   │   │   ├── values-bg/
│   │   │   │   │   │   │   │   │   └── values-bg.xml
│   │   │   │   │   │   │   │   ├── values-bn/
│   │   │   │   │   │   │   │   │   └── values-bn.xml
│   │   │   │   │   │   │   │   ├── values-bs/
│   │   │   │   │   │   │   │   │   └── values-bs.xml
│   │   │   │   │   │   │   │   ├── values-ca/
│   │   │   │   │   │   │   │   │   └── values-ca.xml
│   │   │   │   │   │   │   │   ├── values-cs/
│   │   │   │   │   │   │   │   │   └── values-cs.xml
│   │   │   │   │   │   │   │   ├── values-da/
│   │   │   │   │   │   │   │   │   └── values-da.xml
│   │   │   │   │   │   │   │   ├── values-de/
│   │   │   │   │   │   │   │   │   └── values-de.xml
│   │   │   │   │   │   │   │   ├── values-el/
│   │   │   │   │   │   │   │   │   └── values-el.xml
│   │   │   │   │   │   │   │   ├── values-en-rAU/
│   │   │   │   │   │   │   │   │   └── values-en-rAU.xml
│   │   │   │   │   │   │   │   ├── values-en-rCA/
│   │   │   │   │   │   │   │   │   └── values-en-rCA.xml
│   │   │   │   │   │   │   │   ├── values-en-rGB/
│   │   │   │   │   │   │   │   │   └── values-en-rGB.xml
│   │   │   │   │   │   │   │   ├── values-en-rIN/
│   │   │   │   │   │   │   │   │   └── values-en-rIN.xml
│   │   │   │   │   │   │   │   ├── values-en-rXC/
│   │   │   │   │   │   │   │   │   └── values-en-rXC.xml
│   │   │   │   │   │   │   │   ├── values-es/
│   │   │   │   │   │   │   │   │   └── values-es.xml
│   │   │   │   │   │   │   │   ├── values-es-rUS/
│   │   │   │   │   │   │   │   │   └── values-es-rUS.xml
│   │   │   │   │   │   │   │   ├── values-et/
│   │   │   │   │   │   │   │   │   └── values-et.xml
│   │   │   │   │   │   │   │   ├── values-eu/
│   │   │   │   │   │   │   │   │   └── values-eu.xml
│   │   │   │   │   │   │   │   ├── values-fa/
│   │   │   │   │   │   │   │   │   └── values-fa.xml
│   │   │   │   │   │   │   │   ├── values-fi/
│   │   │   │   │   │   │   │   │   └── values-fi.xml
│   │   │   │   │   │   │   │   ├── values-fr/
│   │   │   │   │   │   │   │   │   └── values-fr.xml
│   │   │   │   │   │   │   │   ├── values-fr-rCA/
│   │   │   │   │   │   │   │   │   └── values-fr-rCA.xml
│   │   │   │   │   │   │   │   ├── values-gl/
│   │   │   │   │   │   │   │   │   └── values-gl.xml
│   │   │   │   │   │   │   │   ├── values-gu/
│   │   │   │   │   │   │   │   │   └── values-gu.xml
│   │   │   │   │   │   │   │   ├── values-hi/
│   │   │   │   │   │   │   │   │   └── values-hi.xml
│   │   │   │   │   │   │   │   ├── values-hr/
│   │   │   │   │   │   │   │   │   └── values-hr.xml
│   │   │   │   │   │   │   │   ├── values-hu/
│   │   │   │   │   │   │   │   │   └── values-hu.xml
│   │   │   │   │   │   │   │   ├── values-hy/
│   │   │   │   │   │   │   │   │   └── values-hy.xml
│   │   │   │   │   │   │   │   ├── values-in/
│   │   │   │   │   │   │   │   │   └── values-in.xml
│   │   │   │   │   │   │   │   ├── values-is/
│   │   │   │   │   │   │   │   │   └── values-is.xml
│   │   │   │   │   │   │   │   ├── values-it/
│   │   │   │   │   │   │   │   │   └── values-it.xml
│   │   │   │   │   │   │   │   ├── values-iw/
│   │   │   │   │   │   │   │   │   └── values-iw.xml
│   │   │   │   │   │   │   │   ├── values-ja/
│   │   │   │   │   │   │   │   │   └── values-ja.xml
│   │   │   │   │   │   │   │   ├── values-ka/
│   │   │   │   │   │   │   │   │   └── values-ka.xml
│   │   │   │   │   │   │   │   ├── values-kk/
│   │   │   │   │   │   │   │   │   └── values-kk.xml
│   │   │   │   │   │   │   │   ├── values-km/
│   │   │   │   │   │   │   │   │   └── values-km.xml
│   │   │   │   │   │   │   │   ├── values-kn/
│   │   │   │   │   │   │   │   │   └── values-kn.xml
│   │   │   │   │   │   │   │   ├── values-ko/
│   │   │   │   │   │   │   │   │   └── values-ko.xml
│   │   │   │   │   │   │   │   ├── values-ky/
│   │   │   │   │   │   │   │   │   └── values-ky.xml
│   │   │   │   │   │   │   │   ├── values-lo/
│   │   │   │   │   │   │   │   │   └── values-lo.xml
│   │   │   │   │   │   │   │   ├── values-lt/
│   │   │   │   │   │   │   │   │   └── values-lt.xml
│   │   │   │   │   │   │   │   ├── values-lv/
│   │   │   │   │   │   │   │   │   └── values-lv.xml
│   │   │   │   │   │   │   │   ├── values-mk/
│   │   │   │   │   │   │   │   │   └── values-mk.xml
│   │   │   │   │   │   │   │   ├── values-ml/
│   │   │   │   │   │   │   │   │   └── values-ml.xml
│   │   │   │   │   │   │   │   ├── values-mn/
│   │   │   │   │   │   │   │   │   └── values-mn.xml
│   │   │   │   │   │   │   │   ├── values-mr/
│   │   │   │   │   │   │   │   │   └── values-mr.xml
│   │   │   │   │   │   │   │   ├── values-ms/
│   │   │   │   │   │   │   │   │   └── values-ms.xml
│   │   │   │   │   │   │   │   ├── values-my/
│   │   │   │   │   │   │   │   │   └── values-my.xml
│   │   │   │   │   │   │   │   ├── values-nb/
│   │   │   │   │   │   │   │   │   └── values-nb.xml
│   │   │   │   │   │   │   │   ├── values-ne/
│   │   │   │   │   │   │   │   │   └── values-ne.xml
│   │   │   │   │   │   │   │   ├── values-nl/
│   │   │   │   │   │   │   │   │   └── values-nl.xml
│   │   │   │   │   │   │   │   ├── values-or/
│   │   │   │   │   │   │   │   │   └── values-or.xml
│   │   │   │   │   │   │   │   ├── values-pa/
│   │   │   │   │   │   │   │   │   └── values-pa.xml
│   │   │   │   │   │   │   │   ├── values-pl/
│   │   │   │   │   │   │   │   │   └── values-pl.xml
│   │   │   │   │   │   │   │   ├── values-pt/
│   │   │   │   │   │   │   │   │   └── values-pt.xml
│   │   │   │   │   │   │   │   ├── values-pt-rBR/
│   │   │   │   │   │   │   │   │   └── values-pt-rBR.xml
│   │   │   │   │   │   │   │   ├── values-pt-rPT/
│   │   │   │   │   │   │   │   │   └── values-pt-rPT.xml
│   │   │   │   │   │   │   │   ├── values-ro/
│   │   │   │   │   │   │   │   │   └── values-ro.xml
│   │   │   │   │   │   │   │   ├── values-ru/
│   │   │   │   │   │   │   │   │   └── values-ru.xml
│   │   │   │   │   │   │   │   ├── values-si/
│   │   │   │   │   │   │   │   │   └── values-si.xml
│   │   │   │   │   │   │   │   ├── values-sk/
│   │   │   │   │   │   │   │   │   └── values-sk.xml
│   │   │   │   │   │   │   │   ├── values-sl/
│   │   │   │   │   │   │   │   │   └── values-sl.xml
│   │   │   │   │   │   │   │   ├── values-sq/
│   │   │   │   │   │   │   │   │   └── values-sq.xml
│   │   │   │   │   │   │   │   ├── values-sr/
│   │   │   │   │   │   │   │   │   └── values-sr.xml
│   │   │   │   │   │   │   │   ├── values-sv/
│   │   │   │   │   │   │   │   │   └── values-sv.xml
│   │   │   │   │   │   │   │   ├── values-sw/
│   │   │   │   │   │   │   │   │   └── values-sw.xml
│   │   │   │   │   │   │   │   ├── values-ta/
│   │   │   │   │   │   │   │   │   └── values-ta.xml
│   │   │   │   │   │   │   │   ├── values-te/
│   │   │   │   │   │   │   │   │   └── values-te.xml
│   │   │   │   │   │   │   │   ├── values-th/
│   │   │   │   │   │   │   │   │   └── values-th.xml
│   │   │   │   │   │   │   │   ├── values-tl/
│   │   │   │   │   │   │   │   │   └── values-tl.xml
│   │   │   │   │   │   │   │   ├── values-tr/
│   │   │   │   │   │   │   │   │   └── values-tr.xml
│   │   │   │   │   │   │   │   ├── values-uk/
│   │   │   │   │   │   │   │   │   └── values-uk.xml
│   │   │   │   │   │   │   │   ├── values-ur/
│   │   │   │   │   │   │   │   │   └── values-ur.xml
│   │   │   │   │   │   │   │   ├── values-uz/
│   │   │   │   │   │   │   │   │   └── values-uz.xml
│   │   │   │   │   │   │   │   ├── values-v21/
│   │   │   │   │   │   │   │   │   └── values-v21.xml
│   │   │   │   │   │   │   │   ├── values-vi/
│   │   │   │   │   │   │   │   │   └── values-vi.xml
│   │   │   │   │   │   │   │   ├── values-zh-rCN/
│   │   │   │   │   │   │   │   │   └── values-zh-rCN.xml
│   │   │   │   │   │   │   │   ├── values-zh-rHK/
│   │   │   │   │   │   │   │   │   └── values-zh-rHK.xml
│   │   │   │   │   │   │   │   ├── values-zh-rTW/
│   │   │   │   │   │   │   │   │   └── values-zh-rTW.xml
│   │   │   │   │   │   │   │   └── values-zu/
│   │   │   │   │   │   │   │       └── values-zu.xml
│   │   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   │   └── packageDebugAndroidTestResources/
│   │   │   │   │   │       ├── merged.dir/
│   │   │   │   │   │       ├── stripped.dir/
│   │   │   │   │   │       │   │   │   │   │   │       └── merger.xml
│   │   │   │   │   ├── debugAndroidTest-mergeJavaRes/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   │   ├── 1ZuCeLYWmYw5MfiXYYnDIw==
│   │   │   │   │   │   │   ├── 3VJ0q7BGqqlcLs7MI_dHlw==
│   │   │   │   │   │   │   ├── 7trlVWptO4XEuV+Pwn+FJg==
│   │   │   │   │   │   │   ├── _hdAXZS0wZVQGsNJYelpuw==
│   │   │   │   │   │   │   ├── Aum8RyvyKLwxGsZvF8wZew==
│   │   │   │   │   │   │   ├── B081BuIDaWSo5McNdeDOlg==
│   │   │   │   │   │   │   ├── BIO8B+IR6k405DzX3KVLfg==
│   │   │   │   │   │   │   ├── eAb6b17AoioR+wuvsUlXUg==
│   │   │   │   │   │   │   ├── EKfNJDNp1qej0g_hpuBvyw==
│   │   │   │   │   │   │   ├── fbJN1jTpNAQTBDjunGpNNw==
│   │   │   │   │   │   │   ├── gMUj5N1rwbVTZNnhBOwS4w==
│   │   │   │   │   │   │   ├── i1fywGgR7739mcd2F7zSGg==
│   │   │   │   │   │   │   ├── IO63ZVBDFar17KQf_eKhoQ==
│   │   │   │   │   │   │   ├── J7CAnpdxVb_3lbUfaCbsMg==
│   │   │   │   │   │   │   ├── J7iYjow924XXI0QA2R4XxA==
│   │   │   │   │   │   │   ├── jrJKw3qGpVbyDMBUg69i1A==
│   │   │   │   │   │   │   ├── kBPjyU0m7mBApb7pIiwI2Q==
│   │   │   │   │   │   │   ├── Kws7Ph2jc1f3d_ecLLw8yA==
│   │   │   │   │   │   │   ├── L+aTIiZM60Y12ESzMr+bBQ==
│   │   │   │   │   │   │   ├── m7ifgdNNq+vMZDPtmarc1Q==
│   │   │   │   │   │   │   ├── MGjkoJZVhVbQnzYuPyFT2g==
│   │   │   │   │   │   │   ├── MoWJlOGWBfjVRC8RvC2PxA==
│   │   │   │   │   │   │   ├── N4JbPtX00mbgf1JBj8hLpA==
│   │   │   │   │   │   │   ├── qD5dgASdHa8TAOro2Bvrtw==
│   │   │   │   │   │   │   ├── qOLoLN95hFQC6sku5Qy+CQ==
│   │   │   │   │   │   │   ├── rrna0K8qZsqmJGvstjZKCQ==
│   │   │   │   │   │   │   ├── tL2pp5i9_KZCC65aeIVhDg==
│   │   │   │   │   │   │   ├── v1FFNCoEO5or6_tvb1ZiNA==
│   │   │   │   │   │   │   └── V8DxNbbYWglX3HsdJ5bXKg==
│   │   │   │   │   │   └── merge-state
│   │   │   │   │   ├── mergeDebugAndroidTestAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugAndroidTestJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeReleaseAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeReleaseJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── packageDebugAndroidTest/tmp/debugAndroidTest/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   │   ├── androidResources
│   │   │   │   │   │   │   └── javaResources0
│   │   │   │   │   │   └── dex-renamer-state.txt
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   │       ├── merged.dir/
│   │   │   │   │       ├── stripped.dir/
│   │   │   │   │       │   │   │   │   │       └── merger.xml
│   │   │   │   ├── java_res/
│   │   │   │   │   ├── debug/processDebugJavaRes/out/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/auth/screens/
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── auth.kotlin_module
│   │   │   │   │   └── release/processReleaseJavaRes/out/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/feature/auth/screens/
│   │   │   │   │       └── META-INF/
│   │   │   │   │           └── auth.kotlin_module
│   │   │   │   ├── library_and_local_jars_jni/debug/copyDebugJniLibsProjectAndLocalJars/jni/
│   │   │   │   ├── library_art_profile/
│   │   │   │   │   ├── debug/prepareDebugArtProfile/
│   │   │   │   │   └── release/prepareReleaseArtProfile/
│   │   │   │   ├── library_jni/
│   │   │   │   │   ├── debug/copyDebugJniLibsProjectOnly/jni/
│   │   │   │   │   └── release/copyReleaseJniLibsProjectOnly/jni/
│   │   │   │   ├── linked_resources_binary_format/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   ├── linked-resources-binary-format.ap_
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── lint_publish_jar/global/prepareLintJarForPublish/
│   │   │   │   ├── local_only_symbol_list/
│   │   │   │   │   ├── debug/parseDebugLocalResources/
│   │   │   │   │   │   └── R-def.txt
│   │   │   │   │   ├── debugAndroidTest/parseDebugAndroidTestLocalResources/
│   │   │   │   │   │   └── R-def.txt
│   │   │   │   │   └── release/parseReleaseLocalResources/
│   │   │   │   │       └── R-def.txt
│   │   │   │   ├── manifest_merge_blame_file/
│   │   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   │   └── manifest-merger-blame-debug-report.txt
│   │   │   │   │   ├── debugAndroidTest/processDebugAndroidTestManifest/
│   │   │   │   │   │   └── manifest-merger-blame-debug-androidTest-report.txt
│   │   │   │   │   └── release/processReleaseManifest/
│   │   │   │   │       └── manifest-merger-blame-release-report.txt
│   │   │   │   ├── merged_consumer_proguard_file/debug/mergeDebugConsumerProguardFiles/
│   │   │   │   ├── merged_java_res/
│   │   │   │   │   ├── debug/mergeDebugJavaResource/
│   │   │   │   │   │   └── feature-auth.jar
│   │   │   │   │   └── debugAndroidTest/mergeDebugAndroidTestJavaResource/
│   │   │   │   │       └── feature-auth.jar
│   │   │   │   ├── merged_jni_libs/
│   │   │   │   │   ├── debug/mergeDebugJniLibFolders/out/
│   │   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestJniLibFolders/out/
│   │   │   │   │   └── release/mergeReleaseJniLibFolders/out/
│   │   │   │   ├── merged_manifest/
│   │   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   │   └── AndroidManifest.xml
│   │   │   │   │   └── release/processReleaseManifest/
│   │   │   │   │       └── AndroidManifest.xml
│   │   │   │   ├── merged_res/debugAndroidTest/mergeDebugAndroidTestResources/
│   │   │   │   │   ├── values-af_values-af.arsc.flat
│   │   │   │   │   ├── values-am_values-am.arsc.flat
│   │   │   │   │   ├── values-ar_values-ar.arsc.flat
│   │   │   │   │   ├── values-as_values-as.arsc.flat
│   │   │   │   │   ├── values-az_values-az.arsc.flat
│   │   │   │   │   ├── values-b+sr+Latn_values-b+sr+Latn.arsc.flat
│   │   │   │   │   ├── values-be_values-be.arsc.flat
│   │   │   │   │   ├── values-bg_values-bg.arsc.flat
│   │   │   │   │   ├── values-bn_values-bn.arsc.flat
│   │   │   │   │   ├── values-bs_values-bs.arsc.flat
│   │   │   │   │   ├── values-ca_values-ca.arsc.flat
│   │   │   │   │   ├── values-cs_values-cs.arsc.flat
│   │   │   │   │   ├── values-da_values-da.arsc.flat
│   │   │   │   │   ├── values-de_values-de.arsc.flat
│   │   │   │   │   ├── values-el_values-el.arsc.flat
│   │   │   │   │   ├── values-en-rAU_values-en-rAU.arsc.flat
│   │   │   │   │   ├── values-en-rCA_values-en-rCA.arsc.flat
│   │   │   │   │   ├── values-en-rGB_values-en-rGB.arsc.flat
│   │   │   │   │   ├── values-en-rIN_values-en-rIN.arsc.flat
│   │   │   │   │   ├── values-en-rXC_values-en-rXC.arsc.flat
│   │   │   │   │   ├── values-es-rUS_values-es-rUS.arsc.flat
│   │   │   │   │   ├── values-es_values-es.arsc.flat
│   │   │   │   │   ├── values-et_values-et.arsc.flat
│   │   │   │   │   ├── values-eu_values-eu.arsc.flat
│   │   │   │   │   ├── values-fa_values-fa.arsc.flat
│   │   │   │   │   ├── values-fi_values-fi.arsc.flat
│   │   │   │   │   ├── values-fr-rCA_values-fr-rCA.arsc.flat
│   │   │   │   │   ├── values-fr_values-fr.arsc.flat
│   │   │   │   │   ├── values-gl_values-gl.arsc.flat
│   │   │   │   │   ├── values-gu_values-gu.arsc.flat
│   │   │   │   │   ├── values-hi_values-hi.arsc.flat
│   │   │   │   │   ├── values-hr_values-hr.arsc.flat
│   │   │   │   │   ├── values-hu_values-hu.arsc.flat
│   │   │   │   │   ├── values-hy_values-hy.arsc.flat
│   │   │   │   │   ├── values-in_values-in.arsc.flat
│   │   │   │   │   ├── values-is_values-is.arsc.flat
│   │   │   │   │   ├── values-it_values-it.arsc.flat
│   │   │   │   │   ├── values-iw_values-iw.arsc.flat
│   │   │   │   │   ├── values-ja_values-ja.arsc.flat
│   │   │   │   │   ├── values-ka_values-ka.arsc.flat
│   │   │   │   │   ├── values-kk_values-kk.arsc.flat
│   │   │   │   │   ├── values-km_values-km.arsc.flat
│   │   │   │   │   ├── values-kn_values-kn.arsc.flat
│   │   │   │   │   ├── values-ko_values-ko.arsc.flat
│   │   │   │   │   ├── values-ky_values-ky.arsc.flat
│   │   │   │   │   ├── values-lo_values-lo.arsc.flat
│   │   │   │   │   ├── values-lt_values-lt.arsc.flat
│   │   │   │   │   ├── values-lv_values-lv.arsc.flat
│   │   │   │   │   ├── values-mk_values-mk.arsc.flat
│   │   │   │   │   ├── values-ml_values-ml.arsc.flat
│   │   │   │   │   ├── values-mn_values-mn.arsc.flat
│   │   │   │   │   ├── values-mr_values-mr.arsc.flat
│   │   │   │   │   ├── values-ms_values-ms.arsc.flat
│   │   │   │   │   ├── values-my_values-my.arsc.flat
│   │   │   │   │   ├── values-nb_values-nb.arsc.flat
│   │   │   │   │   ├── values-ne_values-ne.arsc.flat
│   │   │   │   │   ├── values-nl_values-nl.arsc.flat
│   │   │   │   │   ├── values-or_values-or.arsc.flat
│   │   │   │   │   ├── values-pa_values-pa.arsc.flat
│   │   │   │   │   ├── values-pl_values-pl.arsc.flat
│   │   │   │   │   ├── values-pt-rBR_values-pt-rBR.arsc.flat
│   │   │   │   │   ├── values-pt-rPT_values-pt-rPT.arsc.flat
│   │   │   │   │   ├── values-pt_values-pt.arsc.flat
│   │   │   │   │   ├── values-ro_values-ro.arsc.flat
│   │   │   │   │   ├── values-ru_values-ru.arsc.flat
│   │   │   │   │   ├── values-si_values-si.arsc.flat
│   │   │   │   │   ├── values-sk_values-sk.arsc.flat
│   │   │   │   │   ├── values-sl_values-sl.arsc.flat
│   │   │   │   │   ├── values-sq_values-sq.arsc.flat
│   │   │   │   │   ├── values-sr_values-sr.arsc.flat
│   │   │   │   │   ├── values-sv_values-sv.arsc.flat
│   │   │   │   │   ├── values-sw_values-sw.arsc.flat
│   │   │   │   │   ├── values-ta_values-ta.arsc.flat
│   │   │   │   │   ├── values-te_values-te.arsc.flat
│   │   │   │   │   ├── values-th_values-th.arsc.flat
│   │   │   │   │   ├── values-tl_values-tl.arsc.flat
│   │   │   │   │   ├── values-tr_values-tr.arsc.flat
│   │   │   │   │   ├── values-uk_values-uk.arsc.flat
│   │   │   │   │   ├── values-ur_values-ur.arsc.flat
│   │   │   │   │   ├── values-uz_values-uz.arsc.flat
│   │   │   │   │   ├── values-v21_values-v21.arsc.flat
│   │   │   │   │   ├── values-vi_values-vi.arsc.flat
│   │   │   │   │   ├── values-zh-rCN_values-zh-rCN.arsc.flat
│   │   │   │   │   ├── values-zh-rHK_values-zh-rHK.arsc.flat
│   │   │   │   │   ├── values-zh-rTW_values-zh-rTW.arsc.flat
│   │   │   │   │   ├── values-zu_values-zu.arsc.flat
│   │   │   │   │   └── values_values.arsc.flat
│   │   │   │   ├── merged_res_blame_folder/debugAndroidTest/mergeDebugAndroidTestResources/out/multi-v2/
│   │   │   │   │   ├── mergeDebugAndroidTestResources.json
│   │   │   │   │   ├── values-af.json
│   │   │   │   │   ├── values-am.json
│   │   │   │   │   ├── values-ar.json
│   │   │   │   │   ├── values-as.json
│   │   │   │   │   ├── values-az.json
│   │   │   │   │   ├── values-b+sr+Latn.json
│   │   │   │   │   ├── values-be.json
│   │   │   │   │   ├── values-bg.json
│   │   │   │   │   ├── values-bn.json
│   │   │   │   │   ├── values-bs.json
│   │   │   │   │   ├── values-ca.json
│   │   │   │   │   ├── values-cs.json
│   │   │   │   │   ├── values-da.json
│   │   │   │   │   ├── values-de.json
│   │   │   │   │   ├── values-el.json
│   │   │   │   │   ├── values-en-rAU.json
│   │   │   │   │   ├── values-en-rCA.json
│   │   │   │   │   ├── values-en-rGB.json
│   │   │   │   │   ├── values-en-rIN.json
│   │   │   │   │   ├── values-en-rXC.json
│   │   │   │   │   ├── values-es-rUS.json
│   │   │   │   │   ├── values-es.json
│   │   │   │   │   ├── values-et.json
│   │   │   │   │   ├── values-eu.json
│   │   │   │   │   ├── values-fa.json
│   │   │   │   │   ├── values-fi.json
│   │   │   │   │   ├── values-fr-rCA.json
│   │   │   │   │   ├── values-fr.json
│   │   │   │   │   ├── values-gl.json
│   │   │   │   │   ├── values-gu.json
│   │   │   │   │   ├── values-hi.json
│   │   │   │   │   ├── values-hr.json
│   │   │   │   │   ├── values-hu.json
│   │   │   │   │   ├── values-hy.json
│   │   │   │   │   ├── values-in.json
│   │   │   │   │   ├── values-is.json
│   │   │   │   │   ├── values-it.json
│   │   │   │   │   ├── values-iw.json
│   │   │   │   │   ├── values-ja.json
│   │   │   │   │   ├── values-ka.json
│   │   │   │   │   ├── values-kk.json
│   │   │   │   │   ├── values-km.json
│   │   │   │   │   ├── values-kn.json
│   │   │   │   │   ├── values-ko.json
│   │   │   │   │   ├── values-ky.json
│   │   │   │   │   ├── values-lo.json
│   │   │   │   │   ├── values-lt.json
│   │   │   │   │   ├── values-lv.json
│   │   │   │   │   ├── values-mk.json
│   │   │   │   │   ├── values-ml.json
│   │   │   │   │   ├── values-mn.json
│   │   │   │   │   ├── values-mr.json
│   │   │   │   │   ├── values-ms.json
│   │   │   │   │   ├── values-my.json
│   │   │   │   │   ├── values-nb.json
│   │   │   │   │   ├── values-ne.json
│   │   │   │   │   ├── values-nl.json
│   │   │   │   │   ├── values-or.json
│   │   │   │   │   ├── values-pa.json
│   │   │   │   │   ├── values-pl.json
│   │   │   │   │   ├── values-pt-rBR.json
│   │   │   │   │   ├── values-pt-rPT.json
│   │   │   │   │   ├── values-pt.json
│   │   │   │   │   ├── values-ro.json
│   │   │   │   │   ├── values-ru.json
│   │   │   │   │   ├── values-si.json
│   │   │   │   │   ├── values-sk.json
│   │   │   │   │   ├── values-sl.json
│   │   │   │   │   ├── values-sq.json
│   │   │   │   │   ├── values-sr.json
│   │   │   │   │   ├── values-sv.json
│   │   │   │   │   ├── values-sw.json
│   │   │   │   │   ├── values-ta.json
│   │   │   │   │   ├── values-te.json
│   │   │   │   │   ├── values-th.json
│   │   │   │   │   ├── values-tl.json
│   │   │   │   │   ├── values-tr.json
│   │   │   │   │   ├── values-uk.json
│   │   │   │   │   ├── values-ur.json
│   │   │   │   │   ├── values-uz.json
│   │   │   │   │   ├── values-v21.json
│   │   │   │   │   ├── values-vi.json
│   │   │   │   │   ├── values-zh-rCN.json
│   │   │   │   │   ├── values-zh-rHK.json
│   │   │   │   │   ├── values-zh-rTW.json
│   │   │   │   │   ├── values-zu.json
│   │   │   │   │   └── values.json
│   │   │   │   ├── mixed_scope_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── navigation_json/
│   │   │   │   │   ├── debug/extractDeepLinksDebug/
│   │   │   │   │   │   └── navigation.json
│   │   │   │   │   └── release/extractDeepLinksRelease/
│   │   │   │   │       └── navigation.json
│   │   │   │   ├── navigation_json_for_aar/debug/extractDeepLinksForAarDebug/
│   │   │   │   ├── nested_resources_validation_report/
│   │   │   │   │   ├── debug/generateDebugResources/
│   │   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestResources/
│   │   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   │   └── release/generateReleaseResources/
│   │   │   │   │       └── nestedResourcesValidationReport.txt
│   │   │   │   ├── packaged_manifests/debugAndroidTest/processDebugAndroidTestManifest/
│   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── packaged_res/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   ├── project_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   │   ├── db253dd565055c8fb9a8a67216c68559d57e0a3a40907dfd1aa4d8503687e25e_0.jar
│   │   │   │   │   ├── db253dd565055c8fb9a8a67216c68559d57e0a3a40907dfd1aa4d8503687e25e_1.jar
│   │   │   │   │   ├── db253dd565055c8fb9a8a67216c68559d57e0a3a40907dfd1aa4d8503687e25e_2.jar
│   │   │   │   │   ├── db253dd565055c8fb9a8a67216c68559d57e0a3a40907dfd1aa4d8503687e25e_3.jar
│   │   │   │   │   ├── db253dd565055c8fb9a8a67216c68559d57e0a3a40907dfd1aa4d8503687e25e_4.jar
│   │   │   │   │   └── db253dd565055c8fb9a8a67216c68559d57e0a3a40907dfd1aa4d8503687e25e_5.jar
│   │   │   │   ├── public_res/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   ├── runtime_library_classes_dir/debug/bundleLibRuntimeToDirDebug/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   │   │   ├── screens/
│   │   │   │   │   │   │   ├── ComposableSingletons$LoginScreenKt.class
│   │   │   │   │   │   │   ├── ComposableSingletons$SignupScreenKt.class
│   │   │   │   │   │   │   ├── ComposableSingletons$WelcomeScreenKt.class
│   │   │   │   │   │   │   ├── LoginScreenKt.class
│   │   │   │   │   │   │   ├── PostLoginGateScreenKt.class
│   │   │   │   │   │   │   ├── SignupScreenKt.class
│   │   │   │   │   │   │   └── WelcomeScreenKt.class
│   │   │   │   │   │   └── AuthActionLabelKt.class
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── auth.kotlin_module
│   │   │   │   ├── runtime_library_classes_jar/
│   │   │   │   │   ├── debug/bundleLibRuntimeToJarDebug/
│   │   │   │   │   │   └── classes.jar
│   │   │   │   │   └── release/bundleLibRuntimeToJarRelease/
│   │   │   │   │       └── classes.jar
│   │   │   │   ├── runtime_symbol_list/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── R.txt
│   │   │   │   ├── signing_config_versions/debugAndroidTest/writeDebugAndroidTestSigningConfigVersions/
│   │   │   │   │   └── signing-config-versions.json
│   │   │   │   ├── stable_resource_ids_file/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── stableIds.txt
│   │   │   │   ├── sub_project_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── symbol_list_with_package_name/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── package-aware-r.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── package-aware-r.txt
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── package-aware-r.txt
│   │   │   │   ├── tmp/manifest/androidTest/debug/
│   │   │   │   └── validate_signing_config/debugAndroidTest/validateSigningDebugAndroidTest/
│   │   │   ├── kotlin/
│   │   │   │   ├── compileDebugKotlin/
│   │   │   │   │   ├── cacheable/
│   │   │   │   │   │   ├── caches-jvm/
│   │   │   │   │   │   │   ├── compilerPluginFiles/
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │   │   │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │   │   │   │   ├── inputs/
│   │   │   │   │   │   │   │   ├── source-to-output.tab
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.values.at
│   │   │   │   │   │   │   │   ├── source-to-output.tab_i
│   │   │   │   │   │   │   │   └── source-to-output.tab_i.len
│   │   │   │   │   │   │   ├── jvm/
│   │   │   │   │   │   │   │   └── kotlin/
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │   │   │   │   │       ├── package-parts.tab
│   │   │   │   │   │   │   │       ├── package-parts.tab.keystream
│   │   │   │   │   │   │   │       ├── package-parts.tab.keystream.len
│   │   │   │   │   │   │   │       ├── package-parts.tab.len
│   │   │   │   │   │   │   │       ├── package-parts.tab.values.at
│   │   │   │   │   │   │   │       ├── package-parts.tab_i
│   │   │   │   │   │   │   │       ├── package-parts.tab_i.len
│   │   │   │   │   │   │   │       ├── proto.tab
│   │   │   │   │   │   │   │       ├── proto.tab.keystream
│   │   │   │   │   │   │   │       ├── proto.tab.keystream.len
│   │   │   │   │   │   │   │       ├── proto.tab.len
│   │   │   │   │   │   │   │       ├── proto.tab.values.at
│   │   │   │   │   │   │   │       ├── proto.tab_i
│   │   │   │   │   │   │   │       ├── proto.tab_i.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │   │   │   │   │       ├── source-to-classes.tab_i
│   │   │   │   │   │   │   │       └── source-to-classes.tab_i.len
│   │   │   │   │   │   │   └── lookups/
│   │   │   │   │   │   │       ├── counters.tab
│   │   │   │   │   │   │       ├── file-to-id.tab
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream.len
│   │   │   │   │   │   │       ├── file-to-id.tab.len
│   │   │   │   │   │   │       ├── file-to-id.tab.values.at
│   │   │   │   │   │   │       ├── file-to-id.tab_i
│   │   │   │   │   │   │       ├── file-to-id.tab_i.len
│   │   │   │   │   │   │       ├── id-to-file.tab
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream.len
│   │   │   │   │   │   │       ├── id-to-file.tab.len
│   │   │   │   │   │   │       ├── id-to-file.tab.values.at
│   │   │   │   │   │   │       ├── id-to-file.tab_i
│   │   │   │   │   │   │       ├── id-to-file.tab_i.len
│   │   │   │   │   │   │       ├── lookups.tab
│   │   │   │   │   │   │       ├── lookups.tab.keystream
│   │   │   │   │   │   │       ├── lookups.tab.keystream.len
│   │   │   │   │   │   │       ├── lookups.tab.len
│   │   │   │   │   │   │       ├── lookups.tab.values.at
│   │   │   │   │   │   │       ├── lookups.tab_i
│   │   │   │   │   │   │       └── lookups.tab_i.len
│   │   │   │   │   │   └── last-build.bin
│   │   │   │   │   ├── classpath-snapshot/
│   │   │   │   │   │   └── shrunk-classpath-snapshot.bin
│   │   │   │   │   └── local-state/
│   │   │   │   └── compileReleaseKotlin/
│   │   │   │       ├── cacheable/
│   │   │   │       │   ├── caches-jvm/
│   │   │   │       │   │   ├── compilerPluginFiles/
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │       │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │       │   │   ├── inputs/
│   │   │   │       │   │   │   ├── source-to-output.tab
│   │   │   │       │   │   │   ├── source-to-output.tab.keystream
│   │   │   │       │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │       │   │   │   ├── source-to-output.tab.len
│   │   │   │       │   │   │   ├── source-to-output.tab.values.at
│   │   │   │       │   │   │   ├── source-to-output.tab_i
│   │   │   │       │   │   │   └── source-to-output.tab_i.len
│   │   │   │       │   │   ├── jvm/
│   │   │   │       │   │   │   └── kotlin/
│   │   │   │       │   │   │       ├── internal-name-to-source.tab
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │       │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │       │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │       │   │   │       ├── package-parts.tab
│   │   │   │       │   │   │       ├── package-parts.tab.keystream
│   │   │   │       │   │   │       ├── package-parts.tab.keystream.len
│   │   │   │       │   │   │       ├── package-parts.tab.len
│   │   │   │       │   │   │       ├── package-parts.tab.values.at
│   │   │   │       │   │   │       ├── package-parts.tab_i
│   │   │   │       │   │   │       ├── package-parts.tab_i.len
│   │   │   │       │   │   │       ├── proto.tab
│   │   │   │       │   │   │       ├── proto.tab.keystream
│   │   │   │       │   │   │       ├── proto.tab.keystream.len
│   │   │   │       │   │   │       ├── proto.tab.len
│   │   │   │       │   │   │       ├── proto.tab.values.at
│   │   │   │       │   │   │       ├── proto.tab_i
│   │   │   │       │   │   │       ├── proto.tab_i.len
│   │   │   │       │   │   │       ├── source-to-classes.tab
│   │   │   │       │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │       │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │       │   │   │       ├── source-to-classes.tab.len
│   │   │   │       │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │       │   │   │       ├── source-to-classes.tab_i
│   │   │   │       │   │   │       └── source-to-classes.tab_i.len
│   │   │   │       │   │   └── lookups/
│   │   │   │       │   │       ├── counters.tab
│   │   │   │       │   │       ├── file-to-id.tab
│   │   │   │       │   │       ├── file-to-id.tab.keystream
│   │   │   │       │   │       ├── file-to-id.tab.keystream.len
│   │   │   │       │   │       ├── file-to-id.tab.len
│   │   │   │       │   │       ├── file-to-id.tab.values.at
│   │   │   │       │   │       ├── file-to-id.tab_i
│   │   │   │       │   │       ├── file-to-id.tab_i.len
│   │   │   │       │   │       ├── id-to-file.tab
│   │   │   │       │   │       ├── id-to-file.tab.keystream
│   │   │   │       │   │       ├── id-to-file.tab.keystream.len
│   │   │   │       │   │       ├── id-to-file.tab.len
│   │   │   │       │   │       ├── id-to-file.tab.values.at
│   │   │   │       │   │       ├── id-to-file.tab_i
│   │   │   │       │   │       ├── id-to-file.tab_i.len
│   │   │   │       │   │       ├── lookups.tab
│   │   │   │       │   │       ├── lookups.tab.keystream
│   │   │   │       │   │       ├── lookups.tab.keystream.len
│   │   │   │       │   │       ├── lookups.tab.len
│   │   │   │       │   │       ├── lookups.tab.values.at
│   │   │   │       │   │       ├── lookups.tab_i
│   │   │   │       │   │       └── lookups.tab_i.len
│   │   │   │       │   └── last-build.bin
│   │   │   │       ├── classpath-snapshot/
│   │   │   │       │   └── shrunk-classpath-snapshot.bin
│   │   │   │       └── local-state/
│   │   │   ├── outputs/
│   │   │   │   ├── aar/
│   │   │   │   │   └── auth-debug.aar
│   │   │   │   ├── androidTest-results/connected/debug/
│   │   │   │   ├── apk/androidTest/debug/
│   │   │   │   │   ├── auth-debug-androidTest.apk
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── code_coverage/debugAndroidTest/connected/
│   │   │   │   │   └── coverage.ec
│   │   │   │   ├── connected_android_test_additional_output/debugAndroidTest/connected/
│   │   │   │   └── logs/
│   │   │   │       ├── manifest-merger-debug-report.txt
│   │   │   │       └── manifest-merger-release-report.txt
│   │   │   └── reports/androidTests/connected/debug/
│   │   │       ├── css/
│   │   │       │   ├── base-style.css
│   │   │       │   └── style.css
│   │   │       ├── js/
│   │   │       │   └── report.js
│   │   │       └── index.html
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   ├── navigation/
│   │   │   │   ├── screens/
│   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   ├── PostLoginGateScreen.kt
│   │   │   │   │   ├── SignupScreen.kt
│   │   │   │   │   └── WelcomeScreen.kt
│   │   │   │   └── AuthActionLabel.kt
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── profile/
│   │   ├── build/
│   │   │   ├── .transforms/
│   │   │   │   ├── 0b36982fb3c4548caa06083b9c45e5dc/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 0c885551fbe872ce2439495845ce08bd/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 3082755d8af2ea601b1d3011c72d5a79/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 4768ad8ce949daddd7eabad791883317/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 4c21bc685669c87ad4b042cbce280ee6/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 5edd0a4b8def12cac5cf7336c7b94470/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 795eef7cd57b8fca761e4f75afee39ae/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/profile/navigation/
│   │   │   │   │   │   │   ├── ComposableSingletons$ProfileScreenKt.dex
│   │   │   │   │   │   │   └── ProfileScreenKt.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 8aa4b1c4591e7851f0c4068e6ec00eee/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/profile/navigation/
│   │   │   │   │   │   │   ├── ComposableSingletons$ProfileScreenKt.dex
│   │   │   │   │   │   │   └── ProfileScreenKt.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── 95daba623d34eb9a150169212835a73c/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── c0dded6f7048e0c55957804820e88d9f/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/profile/navigation/
│   │   │   │   │   │   │   ├── ComposableSingletons$ProfileScreenKt.dex
│   │   │   │   │   │   │   └── ProfileScreenKt.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   ├── d08b3efdfa3057461bbf0f151a6435bb/
│   │   │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/profile/navigation/
│   │   │   │   │   │   │   ├── ComposableSingletons$ProfileScreenKt.dex
│   │   │   │   │   │   │   └── ProfileScreenKt.dex
│   │   │   │   │   │   └── desugar_graph.bin
│   │   │   │   │   └── results.bin
│   │   │   │   └── f7643a174c3215e1595843761c1ce313/
│   │   │   │       ├── transformed/bundleLibRuntimeToDirDebug/
│   │   │   │       │   ├── com/miguelrodriguez19/safecube/feature/profile/navigation/
│   │   │   │       │   │   ├── ComposableSingletons$ProfileScreenKt.dex
│   │   │   │       │   │   └── ProfileScreenKt.dex
│   │   │   │       │   └── desugar_graph.bin
│   │   │   │       └── results.bin
│   │   │   ├── generated/
│   │   │   │   ├── res/pngs/
│   │   │   │   │   ├── debug/
│   │   │   │   │   └── release/
│   │   │   │   └── updated_navigation_xml/
│   │   │   │       ├── debug/
│   │   │   │       ├── debugAndroidTest/
│   │   │   │       └── release/
│   │   │   ├── intermediates/
│   │   │   │   ├── aapt_friendly_merged_manifests/
│   │   │   │   │   ├── debug/processDebugManifest/aapt/
│   │   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   │   └── output-metadata.json
│   │   │   │   │   └── release/processReleaseManifest/aapt/
│   │   │   │   │       ├── AndroidManifest.xml
│   │   │   │   │       └── output-metadata.json
│   │   │   │   ├── aar_libs_directory/debug/syncDebugLibJars/libs/
│   │   │   │   ├── aar_main_jar/debug/syncDebugLibJars/
│   │   │   │   │   └── classes.jar
│   │   │   │   ├── aar_metadata/
│   │   │   │   │   ├── debug/writeDebugAarMetadata/
│   │   │   │   │   │   │   │   │   │   │   └── release/writeReleaseAarMetadata/
│   │   │   │   │       │   │   │   │   ├── aar_metadata_check/
│   │   │   │   │   ├── debug/checkDebugAarMetadata/
│   │   │   │   │   └── debugAndroidTest/checkDebugAndroidTestAarMetadata/
│   │   │   │   ├── android_res_source_set_path_map/debugAndroidTest/mapDebugAndroidTestSourceSetPaths/
│   │   │   │   │   └── file-map.txt
│   │   │   │   ├── annotation_processor_list/
│   │   │   │   │   ├── debug/javaPreCompileDebug/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   ├── debugAndroidTest/javaPreCompileDebugAndroidTest/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   ├── debugUnitTest/javaPreCompileDebugUnitTest/
│   │   │   │   │   │   └── annotationProcessors.json
│   │   │   │   │   └── release/javaPreCompileRelease/
│   │   │   │   │       └── annotationProcessors.json
│   │   │   │   ├── annotations_typedef_file/debug/extractDebugAnnotations/
│   │   │   │   │   └── typedefs.txt
│   │   │   │   ├── annotations_zip/debug/extractDebugAnnotations/
│   │   │   │   ├── apk_ide_redirect_file/debugAndroidTest/createDebugAndroidTestApkListingFileRedirect/
│   │   │   │   │   └── redirect.txt
│   │   │   │   ├── assets/
│   │   │   │   │   ├── debug/mergeDebugAssets/
│   │   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestAssets/
│   │   │   │   │   │   └── PublicSuffixDatabase.list
│   │   │   │   │   └── release/mergeReleaseAssets/
│   │   │   │   ├── built_in_kotlinc/
│   │   │   │   │   ├── debug/compileDebugKotlin/classes/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/profile/navigation/
│   │   │   │   │   │   │   ├── ComposableSingletons$ProfileScreenKt.class
│   │   │   │   │   │   │   └── ProfileScreenKt.class
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── profile.kotlin_module
│   │   │   │   │   └── release/compileReleaseKotlin/classes/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/feature/profile/navigation/
│   │   │   │   │       │   ├── ComposableSingletons$ProfileScreenKt.class
│   │   │   │   │       │   └── ProfileScreenKt.class
│   │   │   │   │       └── META-INF/
│   │   │   │   │           └── profile.kotlin_module
│   │   │   │   ├── compile_and_runtime_r_class_jar/
│   │   │   │   │   ├── debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   └── debugUnitTest/generateDebugUnitTestStubRFile/
│   │   │   │   │       └── R.jar
│   │   │   │   ├── compile_library_classes_jar/
│   │   │   │   │   ├── debug/bundleLibCompileToJarDebug/
│   │   │   │   │   │   └── classes.jar
│   │   │   │   │   └── release/bundleLibCompileToJarRelease/
│   │   │   │   │       └── classes.jar
│   │   │   │   ├── compile_r_class_jar/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── R.jar
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── R.jar
│   │   │   │   ├── compile_symbol_list/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── R.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── R.txt
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── R.txt
│   │   │   │   ├── compiled_local_resources/debug/compileDebugLibraryResources/out/
│   │   │   │   ├── compiled_navigation_res/debugAndroidTest/compileDebugAndroidTestNavigationResources/
│   │   │   │   ├── compressed_assets/debugAndroidTest/compressDebugAndroidTestAssets/out/assets/
│   │   │   │   │   └── PublicSuffixDatabase.list.jar
│   │   │   │   ├── consumer_proguard_dir/release/
│   │   │   │   ├── data_binding_layout_info_type_merge/debugAndroidTest/mergeDebugAndroidTestResources/out/
│   │   │   │   ├── data_binding_layout_info_type_package/
│   │   │   │   │   ├── debug/packageDebugResources/out/
│   │   │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/out/
│   │   │   │   │   └── release/packageReleaseResources/out/
│   │   │   │   ├── desugar_graph/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   │   ├── currentProject/
│   │   │   │   │   │   ├── jar_7a5d0bf50883604d89b802eaf27f685a4059a3592bb836e8110256e1c9323f9f_bucket_0/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_7a5d0bf50883604d89b802eaf27f685a4059a3592bb836e8110256e1c9323f9f_bucket_1/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_7a5d0bf50883604d89b802eaf27f685a4059a3592bb836e8110256e1c9323f9f_bucket_2/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_7a5d0bf50883604d89b802eaf27f685a4059a3592bb836e8110256e1c9323f9f_bucket_3/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   ├── jar_7a5d0bf50883604d89b802eaf27f685a4059a3592bb836e8110256e1c9323f9f_bucket_4/
│   │   │   │   │   │   │   └── graph.bin
│   │   │   │   │   │   └── jar_7a5d0bf50883604d89b802eaf27f685a4059a3592bb836e8110256e1c9323f9f_bucket_5/
│   │   │   │   │   │       └── graph.bin
│   │   │   │   │   ├── externalLibs/
│   │   │   │   │   ├── mixedScopes/
│   │   │   │   │   └── otherProjects/
│   │   │   │   ├── dex/debugAndroidTest/
│   │   │   │   │   ├── mergeExtDexDebugAndroidTest/
│   │   │   │   │   │   └── classes.dex
│   │   │   │   │   ├── mergeLibDexDebugAndroidTest/
│   │   │   │   │   │   ├── 0/
│   │   │   │   │   │   ├── 1/
│   │   │   │   │   │   ├── 10/
│   │   │   │   │   │   ├── 11/
│   │   │   │   │   │   ├── 12/
│   │   │   │   │   │   ├── 13/
│   │   │   │   │   │   ├── 14/
│   │   │   │   │   │   ├── 15/
│   │   │   │   │   │   ├── 2/
│   │   │   │   │   │   ├── 3/
│   │   │   │   │   │   ├── 4/
│   │   │   │   │   │   ├── 5/
│   │   │   │   │   │   ├── 6/
│   │   │   │   │   │   ├── 7/
│   │   │   │   │   │   │   └── classes.dex
│   │   │   │   │   │   ├── 8/
│   │   │   │   │   │   └── 9/
│   │   │   │   │   └── mergeProjectDexDebugAndroidTest/
│   │   │   │   │       ├── 0/
│   │   │   │   │       │   └── classes.dex
│   │   │   │   │       ├── 1/
│   │   │   │   │       ├── 10/
│   │   │   │   │       ├── 11/
│   │   │   │   │       ├── 12/
│   │   │   │   │       ├── 13/
│   │   │   │   │       ├── 14/
│   │   │   │   │       ├── 15/
│   │   │   │   │       ├── 2/
│   │   │   │   │       ├── 3/
│   │   │   │   │       ├── 4/
│   │   │   │   │       ├── 5/
│   │   │   │   │       ├── 6/
│   │   │   │   │       ├── 7/
│   │   │   │   │       ├── 8/
│   │   │   │   │       └── 9/
│   │   │   │   ├── dex_archive_input_jar_hashes/debugAndroidTest/dexBuilderDebugAndroidTest/
│   │   │   │   │   └── out
│   │   │   │   ├── dex_number_of_buckets_file/debugAndroidTest/dexBuilderDebugAndroidTest/
│   │   │   │   │   └── out
│   │   │   │   ├── duplicate_classes_check/debugAndroidTest/checkDebugAndroidTestDuplicateClasses/
│   │   │   │   ├── external_file_lib_dex_archives/debugAndroidTest/desugarDebugAndroidTestFileDependencies/
│   │   │   │   ├── external_libs_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── external_libs_dex_archive_with_artifact_transforms/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── generated_proguard_file/
│   │   │   │   │   ├── debug/mergeDebugGeneratedProguardFiles/
│   │   │   │   │   └── release/mergeReleaseGeneratedProguardFiles/
│   │   │   │   ├── incremental/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── debug-mergeJavaRes/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   └── merge-state
│   │   │   │   │   ├── debugAndroidTest/
│   │   │   │   │   │   ├── mergeDebugAndroidTestResources/
│   │   │   │   │   │   │   ├── merged.dir/
│   │   │   │   │   │   │   │   ├── values/
│   │   │   │   │   │   │   │   │   └── values.xml
│   │   │   │   │   │   │   │   ├── values-af/
│   │   │   │   │   │   │   │   │   └── values-af.xml
│   │   │   │   │   │   │   │   ├── values-am/
│   │   │   │   │   │   │   │   │   └── values-am.xml
│   │   │   │   │   │   │   │   ├── values-ar/
│   │   │   │   │   │   │   │   │   └── values-ar.xml
│   │   │   │   │   │   │   │   ├── values-as/
│   │   │   │   │   │   │   │   │   └── values-as.xml
│   │   │   │   │   │   │   │   ├── values-az/
│   │   │   │   │   │   │   │   │   └── values-az.xml
│   │   │   │   │   │   │   │   ├── values-b+sr+Latn/
│   │   │   │   │   │   │   │   │   └── values-b+sr+Latn.xml
│   │   │   │   │   │   │   │   ├── values-be/
│   │   │   │   │   │   │   │   │   └── values-be.xml
│   │   │   │   │   │   │   │   ├── values-bg/
│   │   │   │   │   │   │   │   │   └── values-bg.xml
│   │   │   │   │   │   │   │   ├── values-bn/
│   │   │   │   │   │   │   │   │   └── values-bn.xml
│   │   │   │   │   │   │   │   ├── values-bs/
│   │   │   │   │   │   │   │   │   └── values-bs.xml
│   │   │   │   │   │   │   │   ├── values-ca/
│   │   │   │   │   │   │   │   │   └── values-ca.xml
│   │   │   │   │   │   │   │   ├── values-cs/
│   │   │   │   │   │   │   │   │   └── values-cs.xml
│   │   │   │   │   │   │   │   ├── values-da/
│   │   │   │   │   │   │   │   │   └── values-da.xml
│   │   │   │   │   │   │   │   ├── values-de/
│   │   │   │   │   │   │   │   │   └── values-de.xml
│   │   │   │   │   │   │   │   ├── values-el/
│   │   │   │   │   │   │   │   │   └── values-el.xml
│   │   │   │   │   │   │   │   ├── values-en-rAU/
│   │   │   │   │   │   │   │   │   └── values-en-rAU.xml
│   │   │   │   │   │   │   │   ├── values-en-rCA/
│   │   │   │   │   │   │   │   │   └── values-en-rCA.xml
│   │   │   │   │   │   │   │   ├── values-en-rGB/
│   │   │   │   │   │   │   │   │   └── values-en-rGB.xml
│   │   │   │   │   │   │   │   ├── values-en-rIN/
│   │   │   │   │   │   │   │   │   └── values-en-rIN.xml
│   │   │   │   │   │   │   │   ├── values-en-rXC/
│   │   │   │   │   │   │   │   │   └── values-en-rXC.xml
│   │   │   │   │   │   │   │   ├── values-es/
│   │   │   │   │   │   │   │   │   └── values-es.xml
│   │   │   │   │   │   │   │   ├── values-es-rUS/
│   │   │   │   │   │   │   │   │   └── values-es-rUS.xml
│   │   │   │   │   │   │   │   ├── values-et/
│   │   │   │   │   │   │   │   │   └── values-et.xml
│   │   │   │   │   │   │   │   ├── values-eu/
│   │   │   │   │   │   │   │   │   └── values-eu.xml
│   │   │   │   │   │   │   │   ├── values-fa/
│   │   │   │   │   │   │   │   │   └── values-fa.xml
│   │   │   │   │   │   │   │   ├── values-fi/
│   │   │   │   │   │   │   │   │   └── values-fi.xml
│   │   │   │   │   │   │   │   ├── values-fr/
│   │   │   │   │   │   │   │   │   └── values-fr.xml
│   │   │   │   │   │   │   │   ├── values-fr-rCA/
│   │   │   │   │   │   │   │   │   └── values-fr-rCA.xml
│   │   │   │   │   │   │   │   ├── values-gl/
│   │   │   │   │   │   │   │   │   └── values-gl.xml
│   │   │   │   │   │   │   │   ├── values-gu/
│   │   │   │   │   │   │   │   │   └── values-gu.xml
│   │   │   │   │   │   │   │   ├── values-hi/
│   │   │   │   │   │   │   │   │   └── values-hi.xml
│   │   │   │   │   │   │   │   ├── values-hr/
│   │   │   │   │   │   │   │   │   └── values-hr.xml
│   │   │   │   │   │   │   │   ├── values-hu/
│   │   │   │   │   │   │   │   │   └── values-hu.xml
│   │   │   │   │   │   │   │   ├── values-hy/
│   │   │   │   │   │   │   │   │   └── values-hy.xml
│   │   │   │   │   │   │   │   ├── values-in/
│   │   │   │   │   │   │   │   │   └── values-in.xml
│   │   │   │   │   │   │   │   ├── values-is/
│   │   │   │   │   │   │   │   │   └── values-is.xml
│   │   │   │   │   │   │   │   ├── values-it/
│   │   │   │   │   │   │   │   │   └── values-it.xml
│   │   │   │   │   │   │   │   ├── values-iw/
│   │   │   │   │   │   │   │   │   └── values-iw.xml
│   │   │   │   │   │   │   │   ├── values-ja/
│   │   │   │   │   │   │   │   │   └── values-ja.xml
│   │   │   │   │   │   │   │   ├── values-ka/
│   │   │   │   │   │   │   │   │   └── values-ka.xml
│   │   │   │   │   │   │   │   ├── values-kk/
│   │   │   │   │   │   │   │   │   └── values-kk.xml
│   │   │   │   │   │   │   │   ├── values-km/
│   │   │   │   │   │   │   │   │   └── values-km.xml
│   │   │   │   │   │   │   │   ├── values-kn/
│   │   │   │   │   │   │   │   │   └── values-kn.xml
│   │   │   │   │   │   │   │   ├── values-ko/
│   │   │   │   │   │   │   │   │   └── values-ko.xml
│   │   │   │   │   │   │   │   ├── values-ky/
│   │   │   │   │   │   │   │   │   └── values-ky.xml
│   │   │   │   │   │   │   │   ├── values-lo/
│   │   │   │   │   │   │   │   │   └── values-lo.xml
│   │   │   │   │   │   │   │   ├── values-lt/
│   │   │   │   │   │   │   │   │   └── values-lt.xml
│   │   │   │   │   │   │   │   ├── values-lv/
│   │   │   │   │   │   │   │   │   └── values-lv.xml
│   │   │   │   │   │   │   │   ├── values-mk/
│   │   │   │   │   │   │   │   │   └── values-mk.xml
│   │   │   │   │   │   │   │   ├── values-ml/
│   │   │   │   │   │   │   │   │   └── values-ml.xml
│   │   │   │   │   │   │   │   ├── values-mn/
│   │   │   │   │   │   │   │   │   └── values-mn.xml
│   │   │   │   │   │   │   │   ├── values-mr/
│   │   │   │   │   │   │   │   │   └── values-mr.xml
│   │   │   │   │   │   │   │   ├── values-ms/
│   │   │   │   │   │   │   │   │   └── values-ms.xml
│   │   │   │   │   │   │   │   ├── values-my/
│   │   │   │   │   │   │   │   │   └── values-my.xml
│   │   │   │   │   │   │   │   ├── values-nb/
│   │   │   │   │   │   │   │   │   └── values-nb.xml
│   │   │   │   │   │   │   │   ├── values-ne/
│   │   │   │   │   │   │   │   │   └── values-ne.xml
│   │   │   │   │   │   │   │   ├── values-nl/
│   │   │   │   │   │   │   │   │   └── values-nl.xml
│   │   │   │   │   │   │   │   ├── values-or/
│   │   │   │   │   │   │   │   │   └── values-or.xml
│   │   │   │   │   │   │   │   ├── values-pa/
│   │   │   │   │   │   │   │   │   └── values-pa.xml
│   │   │   │   │   │   │   │   ├── values-pl/
│   │   │   │   │   │   │   │   │   └── values-pl.xml
│   │   │   │   │   │   │   │   ├── values-pt/
│   │   │   │   │   │   │   │   │   └── values-pt.xml
│   │   │   │   │   │   │   │   ├── values-pt-rBR/
│   │   │   │   │   │   │   │   │   └── values-pt-rBR.xml
│   │   │   │   │   │   │   │   ├── values-pt-rPT/
│   │   │   │   │   │   │   │   │   └── values-pt-rPT.xml
│   │   │   │   │   │   │   │   ├── values-ro/
│   │   │   │   │   │   │   │   │   └── values-ro.xml
│   │   │   │   │   │   │   │   ├── values-ru/
│   │   │   │   │   │   │   │   │   └── values-ru.xml
│   │   │   │   │   │   │   │   ├── values-si/
│   │   │   │   │   │   │   │   │   └── values-si.xml
│   │   │   │   │   │   │   │   ├── values-sk/
│   │   │   │   │   │   │   │   │   └── values-sk.xml
│   │   │   │   │   │   │   │   ├── values-sl/
│   │   │   │   │   │   │   │   │   └── values-sl.xml
│   │   │   │   │   │   │   │   ├── values-sq/
│   │   │   │   │   │   │   │   │   └── values-sq.xml
│   │   │   │   │   │   │   │   ├── values-sr/
│   │   │   │   │   │   │   │   │   └── values-sr.xml
│   │   │   │   │   │   │   │   ├── values-sv/
│   │   │   │   │   │   │   │   │   └── values-sv.xml
│   │   │   │   │   │   │   │   ├── values-sw/
│   │   │   │   │   │   │   │   │   └── values-sw.xml
│   │   │   │   │   │   │   │   ├── values-ta/
│   │   │   │   │   │   │   │   │   └── values-ta.xml
│   │   │   │   │   │   │   │   ├── values-te/
│   │   │   │   │   │   │   │   │   └── values-te.xml
│   │   │   │   │   │   │   │   ├── values-th/
│   │   │   │   │   │   │   │   │   └── values-th.xml
│   │   │   │   │   │   │   │   ├── values-tl/
│   │   │   │   │   │   │   │   │   └── values-tl.xml
│   │   │   │   │   │   │   │   ├── values-tr/
│   │   │   │   │   │   │   │   │   └── values-tr.xml
│   │   │   │   │   │   │   │   ├── values-uk/
│   │   │   │   │   │   │   │   │   └── values-uk.xml
│   │   │   │   │   │   │   │   ├── values-ur/
│   │   │   │   │   │   │   │   │   └── values-ur.xml
│   │   │   │   │   │   │   │   ├── values-uz/
│   │   │   │   │   │   │   │   │   └── values-uz.xml
│   │   │   │   │   │   │   │   ├── values-v21/
│   │   │   │   │   │   │   │   │   └── values-v21.xml
│   │   │   │   │   │   │   │   ├── values-vi/
│   │   │   │   │   │   │   │   │   └── values-vi.xml
│   │   │   │   │   │   │   │   ├── values-zh-rCN/
│   │   │   │   │   │   │   │   │   └── values-zh-rCN.xml
│   │   │   │   │   │   │   │   ├── values-zh-rHK/
│   │   │   │   │   │   │   │   │   └── values-zh-rHK.xml
│   │   │   │   │   │   │   │   ├── values-zh-rTW/
│   │   │   │   │   │   │   │   │   └── values-zh-rTW.xml
│   │   │   │   │   │   │   │   └── values-zu/
│   │   │   │   │   │   │   │       └── values-zu.xml
│   │   │   │   │   │   │   ├── stripped.dir/
│   │   │   │   │   │   │   │   │   │   │   │   │   │   └── merger.xml
│   │   │   │   │   │   └── packageDebugAndroidTestResources/
│   │   │   │   │   │       ├── merged.dir/
│   │   │   │   │   │       ├── stripped.dir/
│   │   │   │   │   │       │   │   │   │   │   │       └── merger.xml
│   │   │   │   │   ├── debugAndroidTest-mergeJavaRes/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   │   ├── 1ZuCeLYWmYw5MfiXYYnDIw==
│   │   │   │   │   │   │   ├── 3VJ0q7BGqqlcLs7MI_dHlw==
│   │   │   │   │   │   │   ├── 7trlVWptO4XEuV+Pwn+FJg==
│   │   │   │   │   │   │   ├── _hdAXZS0wZVQGsNJYelpuw==
│   │   │   │   │   │   │   ├── Aum8RyvyKLwxGsZvF8wZew==
│   │   │   │   │   │   │   ├── B081BuIDaWSo5McNdeDOlg==
│   │   │   │   │   │   │   ├── BIO8B+IR6k405DzX3KVLfg==
│   │   │   │   │   │   │   ├── eAb6b17AoioR+wuvsUlXUg==
│   │   │   │   │   │   │   ├── EKfNJDNp1qej0g_hpuBvyw==
│   │   │   │   │   │   │   ├── fbJN1jTpNAQTBDjunGpNNw==
│   │   │   │   │   │   │   ├── gMUj5N1rwbVTZNnhBOwS4w==
│   │   │   │   │   │   │   ├── i1fywGgR7739mcd2F7zSGg==
│   │   │   │   │   │   │   ├── IO63ZVBDFar17KQf_eKhoQ==
│   │   │   │   │   │   │   ├── J7CAnpdxVb_3lbUfaCbsMg==
│   │   │   │   │   │   │   ├── J7iYjow924XXI0QA2R4XxA==
│   │   │   │   │   │   │   ├── jrJKw3qGpVbyDMBUg69i1A==
│   │   │   │   │   │   │   ├── kBPjyU0m7mBApb7pIiwI2Q==
│   │   │   │   │   │   │   ├── Kws7Ph2jc1f3d_ecLLw8yA==
│   │   │   │   │   │   │   ├── L+aTIiZM60Y12ESzMr+bBQ==
│   │   │   │   │   │   │   ├── m7ifgdNNq+vMZDPtmarc1Q==
│   │   │   │   │   │   │   ├── MGjkoJZVhVbQnzYuPyFT2g==
│   │   │   │   │   │   │   ├── MoWJlOGWBfjVRC8RvC2PxA==
│   │   │   │   │   │   │   ├── N4JbPtX00mbgf1JBj8hLpA==
│   │   │   │   │   │   │   ├── qD5dgASdHa8TAOro2Bvrtw==
│   │   │   │   │   │   │   ├── qOLoLN95hFQC6sku5Qy+CQ==
│   │   │   │   │   │   │   ├── rrna0K8qZsqmJGvstjZKCQ==
│   │   │   │   │   │   │   ├── tL2pp5i9_KZCC65aeIVhDg==
│   │   │   │   │   │   │   ├── v1FFNCoEO5or6_tvb1ZiNA==
│   │   │   │   │   │   │   └── V8DxNbbYWglX3HsdJ5bXKg==
│   │   │   │   │   │   └── merge-state
│   │   │   │   │   ├── mergeDebugAndroidTestAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugAndroidTestJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeDebugJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeReleaseAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── mergeReleaseJniLibFolders/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   ├── packageDebugAndroidTest/tmp/debugAndroidTest/
│   │   │   │   │   │   ├── zip-cache/
│   │   │   │   │   │   │   ├── androidResources
│   │   │   │   │   │   │   └── javaResources0
│   │   │   │   │   │   └── dex-renamer-state.txt
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   │       ├── merged.dir/
│   │   │   │   │       ├── stripped.dir/
│   │   │   │   │       │   │   │   │   │       └── merger.xml
│   │   │   │   ├── java_res/
│   │   │   │   │   ├── debug/processDebugJavaRes/out/
│   │   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/profile/navigation/
│   │   │   │   │   │   └── META-INF/
│   │   │   │   │   │       └── profile.kotlin_module
│   │   │   │   │   └── release/processReleaseJavaRes/out/
│   │   │   │   │       ├── com/miguelrodriguez19/safecube/feature/profile/navigation/
│   │   │   │   │       └── META-INF/
│   │   │   │   │           └── profile.kotlin_module
│   │   │   │   ├── library_and_local_jars_jni/debug/copyDebugJniLibsProjectAndLocalJars/jni/
│   │   │   │   ├── library_art_profile/
│   │   │   │   │   ├── debug/prepareDebugArtProfile/
│   │   │   │   │   └── release/prepareReleaseArtProfile/
│   │   │   │   ├── library_jni/
│   │   │   │   │   ├── debug/copyDebugJniLibsProjectOnly/jni/
│   │   │   │   │   └── release/copyReleaseJniLibsProjectOnly/jni/
│   │   │   │   ├── linked_resources_binary_format/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   ├── linked-resources-binary-format.ap_
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── lint_publish_jar/global/prepareLintJarForPublish/
│   │   │   │   ├── local_only_symbol_list/
│   │   │   │   │   ├── debug/parseDebugLocalResources/
│   │   │   │   │   │   └── R-def.txt
│   │   │   │   │   ├── debugAndroidTest/parseDebugAndroidTestLocalResources/
│   │   │   │   │   │   └── R-def.txt
│   │   │   │   │   └── release/parseReleaseLocalResources/
│   │   │   │   │       └── R-def.txt
│   │   │   │   ├── manifest_merge_blame_file/
│   │   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   │   └── manifest-merger-blame-debug-report.txt
│   │   │   │   │   ├── debugAndroidTest/processDebugAndroidTestManifest/
│   │   │   │   │   │   └── manifest-merger-blame-debug-androidTest-report.txt
│   │   │   │   │   └── release/processReleaseManifest/
│   │   │   │   │       └── manifest-merger-blame-release-report.txt
│   │   │   │   ├── merged_consumer_proguard_file/debug/mergeDebugConsumerProguardFiles/
│   │   │   │   ├── merged_java_res/
│   │   │   │   │   ├── debug/mergeDebugJavaResource/
│   │   │   │   │   │   └── feature-profile.jar
│   │   │   │   │   └── debugAndroidTest/mergeDebugAndroidTestJavaResource/
│   │   │   │   │       └── feature-profile.jar
│   │   │   │   ├── merged_jni_libs/
│   │   │   │   │   ├── debug/mergeDebugJniLibFolders/out/
│   │   │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestJniLibFolders/out/
│   │   │   │   │   └── release/mergeReleaseJniLibFolders/out/
│   │   │   │   ├── merged_manifest/
│   │   │   │   │   ├── debug/processDebugManifest/
│   │   │   │   │   │   └── AndroidManifest.xml
│   │   │   │   │   └── release/processReleaseManifest/
│   │   │   │   │       └── AndroidManifest.xml
│   │   │   │   ├── merged_res/debugAndroidTest/mergeDebugAndroidTestResources/
│   │   │   │   │   ├── values-af_values-af.arsc.flat
│   │   │   │   │   ├── values-am_values-am.arsc.flat
│   │   │   │   │   ├── values-ar_values-ar.arsc.flat
│   │   │   │   │   ├── values-as_values-as.arsc.flat
│   │   │   │   │   ├── values-az_values-az.arsc.flat
│   │   │   │   │   ├── values-b+sr+Latn_values-b+sr+Latn.arsc.flat
│   │   │   │   │   ├── values-be_values-be.arsc.flat
│   │   │   │   │   ├── values-bg_values-bg.arsc.flat
│   │   │   │   │   ├── values-bn_values-bn.arsc.flat
│   │   │   │   │   ├── values-bs_values-bs.arsc.flat
│   │   │   │   │   ├── values-ca_values-ca.arsc.flat
│   │   │   │   │   ├── values-cs_values-cs.arsc.flat
│   │   │   │   │   ├── values-da_values-da.arsc.flat
│   │   │   │   │   ├── values-de_values-de.arsc.flat
│   │   │   │   │   ├── values-el_values-el.arsc.flat
│   │   │   │   │   ├── values-en-rAU_values-en-rAU.arsc.flat
│   │   │   │   │   ├── values-en-rCA_values-en-rCA.arsc.flat
│   │   │   │   │   ├── values-en-rGB_values-en-rGB.arsc.flat
│   │   │   │   │   ├── values-en-rIN_values-en-rIN.arsc.flat
│   │   │   │   │   ├── values-en-rXC_values-en-rXC.arsc.flat
│   │   │   │   │   ├── values-es-rUS_values-es-rUS.arsc.flat
│   │   │   │   │   ├── values-es_values-es.arsc.flat
│   │   │   │   │   ├── values-et_values-et.arsc.flat
│   │   │   │   │   ├── values-eu_values-eu.arsc.flat
│   │   │   │   │   ├── values-fa_values-fa.arsc.flat
│   │   │   │   │   ├── values-fi_values-fi.arsc.flat
│   │   │   │   │   ├── values-fr-rCA_values-fr-rCA.arsc.flat
│   │   │   │   │   ├── values-fr_values-fr.arsc.flat
│   │   │   │   │   ├── values-gl_values-gl.arsc.flat
│   │   │   │   │   ├── values-gu_values-gu.arsc.flat
│   │   │   │   │   ├── values-hi_values-hi.arsc.flat
│   │   │   │   │   ├── values-hr_values-hr.arsc.flat
│   │   │   │   │   ├── values-hu_values-hu.arsc.flat
│   │   │   │   │   ├── values-hy_values-hy.arsc.flat
│   │   │   │   │   ├── values-in_values-in.arsc.flat
│   │   │   │   │   ├── values-is_values-is.arsc.flat
│   │   │   │   │   ├── values-it_values-it.arsc.flat
│   │   │   │   │   ├── values-iw_values-iw.arsc.flat
│   │   │   │   │   ├── values-ja_values-ja.arsc.flat
│   │   │   │   │   ├── values-ka_values-ka.arsc.flat
│   │   │   │   │   ├── values-kk_values-kk.arsc.flat
│   │   │   │   │   ├── values-km_values-km.arsc.flat
│   │   │   │   │   ├── values-kn_values-kn.arsc.flat
│   │   │   │   │   ├── values-ko_values-ko.arsc.flat
│   │   │   │   │   ├── values-ky_values-ky.arsc.flat
│   │   │   │   │   ├── values-lo_values-lo.arsc.flat
│   │   │   │   │   ├── values-lt_values-lt.arsc.flat
│   │   │   │   │   ├── values-lv_values-lv.arsc.flat
│   │   │   │   │   ├── values-mk_values-mk.arsc.flat
│   │   │   │   │   ├── values-ml_values-ml.arsc.flat
│   │   │   │   │   ├── values-mn_values-mn.arsc.flat
│   │   │   │   │   ├── values-mr_values-mr.arsc.flat
│   │   │   │   │   ├── values-ms_values-ms.arsc.flat
│   │   │   │   │   ├── values-my_values-my.arsc.flat
│   │   │   │   │   ├── values-nb_values-nb.arsc.flat
│   │   │   │   │   ├── values-ne_values-ne.arsc.flat
│   │   │   │   │   ├── values-nl_values-nl.arsc.flat
│   │   │   │   │   ├── values-or_values-or.arsc.flat
│   │   │   │   │   ├── values-pa_values-pa.arsc.flat
│   │   │   │   │   ├── values-pl_values-pl.arsc.flat
│   │   │   │   │   ├── values-pt-rBR_values-pt-rBR.arsc.flat
│   │   │   │   │   ├── values-pt-rPT_values-pt-rPT.arsc.flat
│   │   │   │   │   ├── values-pt_values-pt.arsc.flat
│   │   │   │   │   ├── values-ro_values-ro.arsc.flat
│   │   │   │   │   ├── values-ru_values-ru.arsc.flat
│   │   │   │   │   ├── values-si_values-si.arsc.flat
│   │   │   │   │   ├── values-sk_values-sk.arsc.flat
│   │   │   │   │   ├── values-sl_values-sl.arsc.flat
│   │   │   │   │   ├── values-sq_values-sq.arsc.flat
│   │   │   │   │   ├── values-sr_values-sr.arsc.flat
│   │   │   │   │   ├── values-sv_values-sv.arsc.flat
│   │   │   │   │   ├── values-sw_values-sw.arsc.flat
│   │   │   │   │   ├── values-ta_values-ta.arsc.flat
│   │   │   │   │   ├── values-te_values-te.arsc.flat
│   │   │   │   │   ├── values-th_values-th.arsc.flat
│   │   │   │   │   ├── values-tl_values-tl.arsc.flat
│   │   │   │   │   ├── values-tr_values-tr.arsc.flat
│   │   │   │   │   ├── values-uk_values-uk.arsc.flat
│   │   │   │   │   ├── values-ur_values-ur.arsc.flat
│   │   │   │   │   ├── values-uz_values-uz.arsc.flat
│   │   │   │   │   ├── values-v21_values-v21.arsc.flat
│   │   │   │   │   ├── values-vi_values-vi.arsc.flat
│   │   │   │   │   ├── values-zh-rCN_values-zh-rCN.arsc.flat
│   │   │   │   │   ├── values-zh-rHK_values-zh-rHK.arsc.flat
│   │   │   │   │   ├── values-zh-rTW_values-zh-rTW.arsc.flat
│   │   │   │   │   ├── values-zu_values-zu.arsc.flat
│   │   │   │   │   └── values_values.arsc.flat
│   │   │   │   ├── merged_res_blame_folder/debugAndroidTest/mergeDebugAndroidTestResources/out/multi-v2/
│   │   │   │   │   ├── mergeDebugAndroidTestResources.json
│   │   │   │   │   ├── values-af.json
│   │   │   │   │   ├── values-am.json
│   │   │   │   │   ├── values-ar.json
│   │   │   │   │   ├── values-as.json
│   │   │   │   │   ├── values-az.json
│   │   │   │   │   ├── values-b+sr+Latn.json
│   │   │   │   │   ├── values-be.json
│   │   │   │   │   ├── values-bg.json
│   │   │   │   │   ├── values-bn.json
│   │   │   │   │   ├── values-bs.json
│   │   │   │   │   ├── values-ca.json
│   │   │   │   │   ├── values-cs.json
│   │   │   │   │   ├── values-da.json
│   │   │   │   │   ├── values-de.json
│   │   │   │   │   ├── values-el.json
│   │   │   │   │   ├── values-en-rAU.json
│   │   │   │   │   ├── values-en-rCA.json
│   │   │   │   │   ├── values-en-rGB.json
│   │   │   │   │   ├── values-en-rIN.json
│   │   │   │   │   ├── values-en-rXC.json
│   │   │   │   │   ├── values-es-rUS.json
│   │   │   │   │   ├── values-es.json
│   │   │   │   │   ├── values-et.json
│   │   │   │   │   ├── values-eu.json
│   │   │   │   │   ├── values-fa.json
│   │   │   │   │   ├── values-fi.json
│   │   │   │   │   ├── values-fr-rCA.json
│   │   │   │   │   ├── values-fr.json
│   │   │   │   │   ├── values-gl.json
│   │   │   │   │   ├── values-gu.json
│   │   │   │   │   ├── values-hi.json
│   │   │   │   │   ├── values-hr.json
│   │   │   │   │   ├── values-hu.json
│   │   │   │   │   ├── values-hy.json
│   │   │   │   │   ├── values-in.json
│   │   │   │   │   ├── values-is.json
│   │   │   │   │   ├── values-it.json
│   │   │   │   │   ├── values-iw.json
│   │   │   │   │   ├── values-ja.json
│   │   │   │   │   ├── values-ka.json
│   │   │   │   │   ├── values-kk.json
│   │   │   │   │   ├── values-km.json
│   │   │   │   │   ├── values-kn.json
│   │   │   │   │   ├── values-ko.json
│   │   │   │   │   ├── values-ky.json
│   │   │   │   │   ├── values-lo.json
│   │   │   │   │   ├── values-lt.json
│   │   │   │   │   ├── values-lv.json
│   │   │   │   │   ├── values-mk.json
│   │   │   │   │   ├── values-ml.json
│   │   │   │   │   ├── values-mn.json
│   │   │   │   │   ├── values-mr.json
│   │   │   │   │   ├── values-ms.json
│   │   │   │   │   ├── values-my.json
│   │   │   │   │   ├── values-nb.json
│   │   │   │   │   ├── values-ne.json
│   │   │   │   │   ├── values-nl.json
│   │   │   │   │   ├── values-or.json
│   │   │   │   │   ├── values-pa.json
│   │   │   │   │   ├── values-pl.json
│   │   │   │   │   ├── values-pt-rBR.json
│   │   │   │   │   ├── values-pt-rPT.json
│   │   │   │   │   ├── values-pt.json
│   │   │   │   │   ├── values-ro.json
│   │   │   │   │   ├── values-ru.json
│   │   │   │   │   ├── values-si.json
│   │   │   │   │   ├── values-sk.json
│   │   │   │   │   ├── values-sl.json
│   │   │   │   │   ├── values-sq.json
│   │   │   │   │   ├── values-sr.json
│   │   │   │   │   ├── values-sv.json
│   │   │   │   │   ├── values-sw.json
│   │   │   │   │   ├── values-ta.json
│   │   │   │   │   ├── values-te.json
│   │   │   │   │   ├── values-th.json
│   │   │   │   │   ├── values-tl.json
│   │   │   │   │   ├── values-tr.json
│   │   │   │   │   ├── values-uk.json
│   │   │   │   │   ├── values-ur.json
│   │   │   │   │   ├── values-uz.json
│   │   │   │   │   ├── values-v21.json
│   │   │   │   │   ├── values-vi.json
│   │   │   │   │   ├── values-zh-rCN.json
│   │   │   │   │   ├── values-zh-rHK.json
│   │   │   │   │   ├── values-zh-rTW.json
│   │   │   │   │   ├── values-zu.json
│   │   │   │   │   └── values.json
│   │   │   │   ├── mixed_scope_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── navigation_json/
│   │   │   │   │   ├── debug/extractDeepLinksDebug/
│   │   │   │   │   │   └── navigation.json
│   │   │   │   │   └── release/extractDeepLinksRelease/
│   │   │   │   │       └── navigation.json
│   │   │   │   ├── navigation_json_for_aar/debug/extractDeepLinksForAarDebug/
│   │   │   │   ├── nested_resources_validation_report/
│   │   │   │   │   ├── debug/generateDebugResources/
│   │   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestResources/
│   │   │   │   │   │   └── nestedResourcesValidationReport.txt
│   │   │   │   │   └── release/generateReleaseResources/
│   │   │   │   │       └── nestedResourcesValidationReport.txt
│   │   │   │   ├── packaged_manifests/debugAndroidTest/processDebugAndroidTestManifest/
│   │   │   │   │   ├── AndroidManifest.xml
│   │   │   │   │   └── output-metadata.json
│   │   │   │   ├── packaged_res/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   ├── project_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   │   ├── 54942177c1f44078d0652d9efa422be844af988eb1759676df9980c106e05f5e_0.jar
│   │   │   │   │   ├── 54942177c1f44078d0652d9efa422be844af988eb1759676df9980c106e05f5e_1.jar
│   │   │   │   │   ├── 54942177c1f44078d0652d9efa422be844af988eb1759676df9980c106e05f5e_2.jar
│   │   │   │   │   ├── 54942177c1f44078d0652d9efa422be844af988eb1759676df9980c106e05f5e_3.jar
│   │   │   │   │   ├── 54942177c1f44078d0652d9efa422be844af988eb1759676df9980c106e05f5e_4.jar
│   │   │   │   │   └── 54942177c1f44078d0652d9efa422be844af988eb1759676df9980c106e05f5e_5.jar
│   │   │   │   ├── public_res/
│   │   │   │   │   ├── debug/packageDebugResources/
│   │   │   │   │   └── release/packageReleaseResources/
│   │   │   │   ├── runtime_library_classes_dir/debug/bundleLibRuntimeToDirDebug/
│   │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/profile/navigation/
│   │   │   │   │   │   ├── ComposableSingletons$ProfileScreenKt.class
│   │   │   │   │   │   └── ProfileScreenKt.class
│   │   │   │   │   └── META-INF/
│   │   │   │   │       └── profile.kotlin_module
│   │   │   │   ├── runtime_library_classes_jar/
│   │   │   │   │   ├── debug/bundleLibRuntimeToJarDebug/
│   │   │   │   │   │   └── classes.jar
│   │   │   │   │   └── release/bundleLibRuntimeToJarRelease/
│   │   │   │   │       └── classes.jar
│   │   │   │   ├── runtime_symbol_list/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── R.txt
│   │   │   │   ├── signing_config_versions/debugAndroidTest/writeDebugAndroidTestSigningConfigVersions/
│   │   │   │   │   └── signing-config-versions.json
│   │   │   │   ├── stable_resource_ids_file/debugAndroidTest/processDebugAndroidTestResources/
│   │   │   │   │   └── stableIds.txt
│   │   │   │   ├── sub_project_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│   │   │   │   ├── symbol_list_with_package_name/
│   │   │   │   │   ├── debug/generateDebugRFile/
│   │   │   │   │   │   └── package-aware-r.txt
│   │   │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│   │   │   │   │   │   └── package-aware-r.txt
│   │   │   │   │   └── release/generateReleaseRFile/
│   │   │   │   │       └── package-aware-r.txt
│   │   │   │   ├── tmp/manifest/androidTest/debug/
│   │   │   │   └── validate_signing_config/debugAndroidTest/validateSigningDebugAndroidTest/
│   │   │   ├── kotlin/
│   │   │   │   ├── compileDebugKotlin/
│   │   │   │   │   ├── cacheable/
│   │   │   │   │   │   ├── caches-jvm/
│   │   │   │   │   │   │   ├── compilerPluginFiles/
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │   │   │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │   │   │   │   ├── inputs/
│   │   │   │   │   │   │   │   ├── source-to-output.tab
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream
│   │   │   │   │   │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.len
│   │   │   │   │   │   │   │   ├── source-to-output.tab.values.at
│   │   │   │   │   │   │   │   ├── source-to-output.tab_i
│   │   │   │   │   │   │   │   └── source-to-output.tab_i.len
│   │   │   │   │   │   │   ├── jvm/
│   │   │   │   │   │   │   │   └── kotlin/
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │   │   │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │   │   │   │   │       ├── package-parts.tab
│   │   │   │   │   │   │   │       ├── package-parts.tab.keystream
│   │   │   │   │   │   │   │       ├── package-parts.tab.keystream.len
│   │   │   │   │   │   │   │       ├── package-parts.tab.len
│   │   │   │   │   │   │   │       ├── package-parts.tab.values.at
│   │   │   │   │   │   │   │       ├── package-parts.tab_i
│   │   │   │   │   │   │   │       ├── package-parts.tab_i.len
│   │   │   │   │   │   │   │       ├── proto.tab
│   │   │   │   │   │   │   │       ├── proto.tab.keystream
│   │   │   │   │   │   │   │       ├── proto.tab.keystream.len
│   │   │   │   │   │   │   │       ├── proto.tab.len
│   │   │   │   │   │   │   │       ├── proto.tab.values.at
│   │   │   │   │   │   │   │       ├── proto.tab_i
│   │   │   │   │   │   │   │       ├── proto.tab_i.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.len
│   │   │   │   │   │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │   │   │   │   │       ├── source-to-classes.tab_i
│   │   │   │   │   │   │   │       └── source-to-classes.tab_i.len
│   │   │   │   │   │   │   └── lookups/
│   │   │   │   │   │   │       ├── counters.tab
│   │   │   │   │   │   │       ├── file-to-id.tab
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream
│   │   │   │   │   │   │       ├── file-to-id.tab.keystream.len
│   │   │   │   │   │   │       ├── file-to-id.tab.len
│   │   │   │   │   │   │       ├── file-to-id.tab.values.at
│   │   │   │   │   │   │       ├── file-to-id.tab_i
│   │   │   │   │   │   │       ├── file-to-id.tab_i.len
│   │   │   │   │   │   │       ├── id-to-file.tab
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream
│   │   │   │   │   │   │       ├── id-to-file.tab.keystream.len
│   │   │   │   │   │   │       ├── id-to-file.tab.len
│   │   │   │   │   │   │       ├── id-to-file.tab.values.at
│   │   │   │   │   │   │       ├── id-to-file.tab_i.len
│   │   │   │   │   │   │       ├── lookups.tab
│   │   │   │   │   │   │       ├── lookups.tab.keystream
│   │   │   │   │   │   │       ├── lookups.tab.keystream.len
│   │   │   │   │   │   │       ├── lookups.tab.len
│   │   │   │   │   │   │       ├── lookups.tab.values.at
│   │   │   │   │   │   │       ├── lookups.tab_i
│   │   │   │   │   │   │       └── lookups.tab_i.len
│   │   │   │   │   │   └── last-build.bin
│   │   │   │   │   ├── classpath-snapshot/
│   │   │   │   │   │   └── shrunk-classpath-snapshot.bin
│   │   │   │   │   └── local-state/
│   │   │   │   └── compileReleaseKotlin/
│   │   │   │       ├── cacheable/
│   │   │   │       │   ├── caches-jvm/
│   │   │   │       │   │   ├── compilerPluginFiles/
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.len
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│   │   │   │       │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.len
│   │   │   │       │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│   │   │   │       │   │   │   └── sources-referenced-by-plugins.tab_i.len
│   │   │   │       │   │   ├── inputs/
│   │   │   │       │   │   │   ├── source-to-output.tab
│   │   │   │       │   │   │   ├── source-to-output.tab.keystream
│   │   │   │       │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │       │   │   │   ├── source-to-output.tab.len
│   │   │   │       │   │   │   ├── source-to-output.tab.values.at
│   │   │   │       │   │   │   ├── source-to-output.tab_i
│   │   │   │       │   │   │   └── source-to-output.tab_i.len
│   │   │   │       │   │   ├── jvm/
│   │   │   │       │   │   │   └── kotlin/
│   │   │   │       │   │   │       ├── internal-name-to-source.tab
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.len
│   │   │   │       │   │   │       ├── internal-name-to-source.tab.values.at
│   │   │   │       │   │   │       ├── internal-name-to-source.tab_i
│   │   │   │       │   │   │       ├── internal-name-to-source.tab_i.len
│   │   │   │       │   │   │       ├── package-parts.tab
│   │   │   │       │   │   │       ├── package-parts.tab.keystream
│   │   │   │       │   │   │       ├── package-parts.tab.keystream.len
│   │   │   │       │   │   │       ├── package-parts.tab.len
│   │   │   │       │   │   │       ├── package-parts.tab.values.at
│   │   │   │       │   │   │       ├── package-parts.tab_i
│   │   │   │       │   │   │       ├── package-parts.tab_i.len
│   │   │   │       │   │   │       ├── proto.tab
│   │   │   │       │   │   │       ├── proto.tab.keystream
│   │   │   │       │   │   │       ├── proto.tab.keystream.len
│   │   │   │       │   │   │       ├── proto.tab.len
│   │   │   │       │   │   │       ├── proto.tab.values.at
│   │   │   │       │   │   │       ├── proto.tab_i
│   │   │   │       │   │   │       ├── proto.tab_i.len
│   │   │   │       │   │   │       ├── source-to-classes.tab
│   │   │   │       │   │   │       ├── source-to-classes.tab.keystream
│   │   │   │       │   │   │       ├── source-to-classes.tab.keystream.len
│   │   │   │       │   │   │       ├── source-to-classes.tab.len
│   │   │   │       │   │   │       ├── source-to-classes.tab.values.at
│   │   │   │       │   │   │       ├── source-to-classes.tab_i
│   │   │   │       │   │   │       └── source-to-classes.tab_i.len
│   │   │   │       │   │   └── lookups/
│   │   │   │       │   │       ├── counters.tab
│   │   │   │       │   │       ├── file-to-id.tab
│   │   │   │       │   │       ├── file-to-id.tab.keystream
│   │   │   │       │   │       ├── file-to-id.tab.keystream.len
│   │   │   │       │   │       ├── file-to-id.tab.len
│   │   │   │       │   │       ├── file-to-id.tab.values.at
│   │   │   │       │   │       ├── file-to-id.tab_i
│   │   │   │       │   │       ├── file-to-id.tab_i.len
│   │   │   │       │   │       ├── id-to-file.tab
│   │   │   │       │   │       ├── id-to-file.tab.keystream
│   │   │   │       │   │       ├── id-to-file.tab.keystream.len
│   │   │   │       │   │       ├── id-to-file.tab.len
│   │   │   │       │   │       ├── id-to-file.tab.values.at
│   │   │   │       │   │       ├── id-to-file.tab_i.len
│   │   │   │       │   │       ├── lookups.tab
│   │   │   │       │   │       ├── lookups.tab.keystream
│   │   │   │       │   │       ├── lookups.tab.keystream.len
│   │   │   │       │   │       ├── lookups.tab.len
│   │   │   │       │   │       ├── lookups.tab.values.at
│   │   │   │       │   │       ├── lookups.tab_i
│   │   │   │       │   │       └── lookups.tab_i.len
│   │   │   │       │   └── last-build.bin
│   │   │   │       ├── classpath-snapshot/
│   │   │   │       │   └── shrunk-classpath-snapshot.bin
│   │   │   │       └── local-state/
│   │   │   ├── outputs/
│   │   │   │   ├── aar/
│   │   │   │   │   └── profile-debug.aar
│   │   │   │   ├── androidTest-results/connected/debug/
│   │   │   │   ├── apk/androidTest/debug/
│   │   │   │   │   ├── output-metadata.json
│   │   │   │   │   └── profile-debug-androidTest.apk
│   │   │   │   ├── code_coverage/debugAndroidTest/connected/
│   │   │   │   │   └── coverage.ec
│   │   │   │   ├── connected_android_test_additional_output/debugAndroidTest/connected/
│   │   │   │   └── logs/
│   │   │   │       ├── manifest-merger-debug-report.txt
│   │   │   │       └── manifest-merger-release-report.txt
│   │   │   └── reports/androidTests/connected/debug/
│   │   │       ├── css/
│   │   │       │   ├── base-style.css
│   │   │       │   └── style.css
│   │   │       ├── js/
│   │   │       │   └── report.js
│   │   │       └── index.html
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/feature/profile/navigation/
│   │   │   │   └── ProfileScreen.kt
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── vault/
│       ├── build/
│       │   ├── .transforms/
│       │   │   ├── 27d0b81050cfdd9c50d2e5b055bb6c1c/
│       │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│       │   │   │   │   └── desugar_graph.bin
│       │   │   │   └── results.bin
│       │   │   ├── 360469c4bb368e7c1c0b61b3fe8550a5/
│       │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│       │   │   │   │   └── desugar_graph.bin
│       │   │   │   └── results.bin
│       │   │   ├── 4277804b7ef9e36615b7e708508398dd/
│       │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│       │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/vault/navigation/
│       │   │   │   │   │   ├── AppTab.dex
│       │   │   │   │   │   ├── ComposableSingletons$CreateVaultScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$NavigationBarKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$RecoveryKeyScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$SettingsScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$UnlockVaultScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$VaultFoldersScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$VaultScreenKt.dex
│       │   │   │   │   │   ├── CreateVaultScreenKt.dex
│       │   │   │   │   │   ├── NavigationBarKt.dex
│       │   │   │   │   │   ├── RecoveryKeyScreenKt.dex
│       │   │   │   │   │   ├── SettingsScreenKt.dex
│       │   │   │   │   │   ├── UnlockVaultScreenKt.dex
│       │   │   │   │   │   ├── VaultFoldersScreenKt.dex
│       │   │   │   │   │   └── VaultScreenKt.dex
│       │   │   │   │   └── desugar_graph.bin
│       │   │   │   └── results.bin
│       │   │   ├── 44f91581263bb3649bd183017d502903/
│       │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│       │   │   │   │   └── desugar_graph.bin
│       │   │   │   └── results.bin
│       │   │   ├── 530ac381b1148ef86fcd9b2d01ff659c/
│       │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│       │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/vault/navigation/
│       │   │   │   │   │   ├── AppTab.dex
│       │   │   │   │   │   ├── ComposableSingletons$CreateVaultScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$NavigationBarKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$RecoveryKeyScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$SettingsScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$UnlockVaultScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$VaultFoldersScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$VaultScreenKt.dex
│       │   │   │   │   │   ├── CreateVaultScreenKt.dex
│       │   │   │   │   │   ├── NavigationBarKt.dex
│       │   │   │   │   │   ├── RecoveryKeyScreenKt.dex
│       │   │   │   │   │   ├── SettingsScreenKt.dex
│       │   │   │   │   │   ├── UnlockVaultScreenKt.dex
│       │   │   │   │   │   ├── VaultFoldersScreenKt.dex
│       │   │   │   │   │   └── VaultScreenKt.dex
│       │   │   │   │   └── desugar_graph.bin
│       │   │   │   └── results.bin
│       │   │   ├── 717e4635debfdeaec981353de4ee428f/
│       │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│       │   │   │   │   └── desugar_graph.bin
│       │   │   │   └── results.bin
│       │   │   ├── b93a3d365ed35f5435e3f8772b2903e6/
│       │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│       │   │   │   │   └── desugar_graph.bin
│       │   │   │   └── results.bin
│       │   │   ├── bb0386f336c616b0a02349f9d8fac4bb/
│       │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│       │   │   │   │   └── desugar_graph.bin
│       │   │   │   └── results.bin
│       │   │   ├── bb82259e389d41526dad586601f074b1/
│       │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│       │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/vault/navigation/
│       │   │   │   │   │   ├── ComposableSingletons$VaultPlaceholderScreensKt.dex
│       │   │   │   │   │   └── VaultPlaceholderScreensKt.dex
│       │   │   │   │   └── desugar_graph.bin
│       │   │   │   └── results.bin
│       │   │   ├── d16a41f61c5af75706f20920fcb14dbf/
│       │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│       │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/vault/navigation/
│       │   │   │   │   │   ├── AppTab.dex
│       │   │   │   │   │   ├── ComposableSingletons$CreateVaultScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$NavigationBarKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$RecoveryKeyScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$SettingsScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$UnlockVaultScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$VaultFoldersScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$VaultScreenKt.dex
│       │   │   │   │   │   ├── CreateVaultScreenKt.dex
│       │   │   │   │   │   ├── NavigationBarKt.dex
│       │   │   │   │   │   ├── RecoveryKeyScreenKt.dex
│       │   │   │   │   │   ├── SettingsScreenKt.dex
│       │   │   │   │   │   ├── UnlockVaultScreenKt.dex
│       │   │   │   │   │   ├── VaultFoldersScreenKt.dex
│       │   │   │   │   │   └── VaultScreenKt.dex
│       │   │   │   │   └── desugar_graph.bin
│       │   │   │   └── results.bin
│       │   │   ├── da53ee2e68e4831ca4bfad8e78dd30e3/
│       │   │   │   ├── transformed/bundleLibRuntimeToDirDebug/
│       │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/vault/navigation/
│       │   │   │   │   │   ├── AppBottomBarKt.dex
│       │   │   │   │   │   ├── AppScreenKt.dex
│       │   │   │   │   │   ├── AppTab.dex
│       │   │   │   │   │   ├── ComposableSingletons$AppBottomBarKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$AppScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$CreateVaultScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$RecoveryKeyScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$SettingsScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$UnlockVaultScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$VaultFoldersScreenKt.dex
│       │   │   │   │   │   ├── ComposableSingletons$VaultScreenKt.dex
│       │   │   │   │   │   ├── CreateVaultScreenKt.dex
│       │   │   │   │   │   ├── RecoveryKeyScreenKt.dex
│       │   │   │   │   │   ├── SettingsScreenKt.dex
│       │   │   │   │   │   ├── UnlockVaultScreenKt.dex
│       │   │   │   │   │   ├── VaultFoldersScreenKt.dex
│       │   │   │   │   │   └── VaultScreenKt.dex
│       │   │   │   │   └── desugar_graph.bin
│       │   │   │   └── results.bin
│       │   │   └── e5dda88742488ff562d66c22ce56e7da/
│       │   │       ├── transformed/bundleLibRuntimeToDirDebug/
│       │   │       │   ├── com/miguelrodriguez19/safecube/feature/vault/navigation/
│       │   │       │   │   ├── AppTab.dex
│       │   │       │   │   ├── ComposableSingletons$CreateVaultScreenKt.dex
│       │   │       │   │   ├── ComposableSingletons$NavigationBarKt.dex
│       │   │       │   │   ├── ComposableSingletons$RecoveryKeyScreenKt.dex
│       │   │       │   │   ├── ComposableSingletons$SettingsScreenKt.dex
│       │   │       │   │   ├── ComposableSingletons$UnlockVaultScreenKt.dex
│       │   │       │   │   ├── ComposableSingletons$VaultFoldersScreenKt.dex
│       │   │       │   │   ├── ComposableSingletons$VaultScreenKt.dex
│       │   │       │   │   ├── CreateVaultScreenKt.dex
│       │   │       │   │   ├── NavigationBarKt.dex
│       │   │       │   │   ├── RecoveryKeyScreenKt.dex
│       │   │       │   │   ├── SettingsScreenKt.dex
│       │   │       │   │   ├── UnlockVaultScreenKt.dex
│       │   │       │   │   ├── VaultFoldersScreenKt.dex
│       │   │       │   │   └── VaultScreenKt.dex
│       │   │       │   └── desugar_graph.bin
│       │   │       └── results.bin
│       │   ├── generated/
│       │   │   ├── res/pngs/
│       │   │   │   ├── debug/
│       │   │   │   └── release/
│       │   │   └── updated_navigation_xml/
│       │   │       ├── debug/
│       │   │       ├── debugAndroidTest/
│       │   │       └── release/
│       │   ├── intermediates/
│       │   │   ├── aapt_friendly_merged_manifests/
│       │   │   │   ├── debug/processDebugManifest/aapt/
│       │   │   │   │   ├── AndroidManifest.xml
│       │   │   │   │   └── output-metadata.json
│       │   │   │   └── release/processReleaseManifest/aapt/
│       │   │   │       ├── AndroidManifest.xml
│       │   │   │       └── output-metadata.json
│       │   │   ├── aar_libs_directory/debug/syncDebugLibJars/libs/
│       │   │   ├── aar_main_jar/debug/syncDebugLibJars/
│       │   │   │   └── classes.jar
│       │   │   ├── aar_metadata/
│       │   │   │   ├── debug/writeDebugAarMetadata/
│       │   │   │   │   │       │   │   │   └── release/writeReleaseAarMetadata/
│       │   │   │       │       │   │   ├── aar_metadata_check/
│       │   │   │   ├── debug/checkDebugAarMetadata/
│       │   │   │   └── debugAndroidTest/checkDebugAndroidTestAarMetadata/
│       │   │   ├── android_res_source_set_path_map/debugAndroidTest/mapDebugAndroidTestSourceSetPaths/
│       │   │   │   └── file-map.txt
│       │   │   ├── annotation_processor_list/
│       │   │   │   ├── debug/javaPreCompileDebug/
│       │   │   │   │   └── annotationProcessors.json
│       │   │   │   ├── debugAndroidTest/javaPreCompileDebugAndroidTest/
│       │   │   │   │   └── annotationProcessors.json
│       │   │   │   ├── debugUnitTest/javaPreCompileDebugUnitTest/
│       │   │   │   │   └── annotationProcessors.json
│       │   │   │   └── release/javaPreCompileRelease/
│       │   │   │       └── annotationProcessors.json
│       │   │   ├── annotations_typedef_file/debug/extractDebugAnnotations/
│       │   │   │   └── typedefs.txt
│       │   │   ├── annotations_zip/debug/extractDebugAnnotations/
│       │   │   ├── apk_ide_redirect_file/debugAndroidTest/createDebugAndroidTestApkListingFileRedirect/
│       │   │   │   └── redirect.txt
│       │   │   ├── assets/
│       │   │   │   ├── debug/mergeDebugAssets/
│       │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestAssets/
│       │   │   │   │   └── PublicSuffixDatabase.list
│       │   │   │   └── release/mergeReleaseAssets/
│       │   │   ├── built_in_kotlinc/
│       │   │   │   ├── debug/compileDebugKotlin/classes/
│       │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/vault/navigation/
│       │   │   │   │   │   ├── AppTab.class
│       │   │   │   │   │   ├── ComposableSingletons$CreateVaultScreenKt.class
│       │   │   │   │   │   ├── ComposableSingletons$NavigationBarKt.class
│       │   │   │   │   │   ├── ComposableSingletons$RecoveryKeyScreenKt.class
│       │   │   │   │   │   ├── ComposableSingletons$SettingsScreenKt.class
│       │   │   │   │   │   ├── ComposableSingletons$UnlockVaultScreenKt.class
│       │   │   │   │   │   ├── ComposableSingletons$VaultFoldersScreenKt.class
│       │   │   │   │   │   ├── ComposableSingletons$VaultScreenKt.class
│       │   │   │   │   │   ├── CreateVaultScreenKt.class
│       │   │   │   │   │   ├── NavigationBarKt.class
│       │   │   │   │   │   ├── RecoveryKeyScreenKt.class
│       │   │   │   │   │   ├── SettingsScreenKt.class
│       │   │   │   │   │   ├── UnlockVaultScreenKt.class
│       │   │   │   │   │   ├── VaultFoldersScreenKt.class
│       │   │   │   │   │   └── VaultScreenKt.class
│       │   │   │   │   └── META-INF/
│       │   │   │   │       └── vault.kotlin_module
│       │   │   │   └── release/compileReleaseKotlin/classes/
│       │   │   │       ├── com/miguelrodriguez19/safecube/feature/vault/navigation/
│       │   │   │       │   ├── AppTab.class
│       │   │   │       │   ├── ComposableSingletons$CreateVaultScreenKt.class
│       │   │   │       │   ├── ComposableSingletons$NavigationBarKt.class
│       │   │   │       │   ├── ComposableSingletons$RecoveryKeyScreenKt.class
│       │   │   │       │   ├── ComposableSingletons$SettingsScreenKt.class
│       │   │   │       │   ├── ComposableSingletons$UnlockVaultScreenKt.class
│       │   │   │       │   ├── ComposableSingletons$VaultFoldersScreenKt.class
│       │   │   │       │   ├── ComposableSingletons$VaultScreenKt.class
│       │   │   │       │   ├── CreateVaultScreenKt.class
│       │   │   │       │   ├── NavigationBarKt.class
│       │   │   │       │   ├── RecoveryKeyScreenKt.class
│       │   │   │       │   ├── SettingsScreenKt.class
│       │   │   │       │   ├── UnlockVaultScreenKt.class
│       │   │   │       │   ├── VaultFoldersScreenKt.class
│       │   │   │       │   └── VaultScreenKt.class
│       │   │   │       └── META-INF/
│       │   │   │           └── vault.kotlin_module
│       │   │   ├── compile_and_runtime_r_class_jar/
│       │   │   │   ├── debugAndroidTest/processDebugAndroidTestResources/
│       │   │   │   │   └── R.jar
│       │   │   │   └── debugUnitTest/generateDebugUnitTestStubRFile/
│       │   │   │       └── R.jar
│       │   │   ├── compile_library_classes_jar/
│       │   │   │   ├── debug/bundleLibCompileToJarDebug/
│       │   │   │   │   └── classes.jar
│       │   │   │   └── release/bundleLibCompileToJarRelease/
│       │   │   │       └── classes.jar
│       │   │   ├── compile_r_class_jar/
│       │   │   │   ├── debug/generateDebugRFile/
│       │   │   │   │   └── R.jar
│       │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│       │   │   │   │   └── R.jar
│       │   │   │   └── release/generateReleaseRFile/
│       │   │   │       └── R.jar
│       │   │   ├── compile_symbol_list/
│       │   │   │   ├── debug/generateDebugRFile/
│       │   │   │   │   └── R.txt
│       │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│       │   │   │   │   └── R.txt
│       │   │   │   └── release/generateReleaseRFile/
│       │   │   │       └── R.txt
│       │   │   ├── compiled_local_resources/debug/compileDebugLibraryResources/out/
│       │   │   ├── compiled_navigation_res/debugAndroidTest/compileDebugAndroidTestNavigationResources/
│       │   │   ├── compressed_assets/debugAndroidTest/compressDebugAndroidTestAssets/out/assets/
│       │   │   │   └── PublicSuffixDatabase.list.jar
│       │   │   ├── consumer_proguard_dir/release/
│       │   │   ├── data_binding_layout_info_type_merge/debugAndroidTest/mergeDebugAndroidTestResources/out/
│       │   │   ├── data_binding_layout_info_type_package/
│       │   │   │   ├── debug/packageDebugResources/out/
│       │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/out/
│       │   │   │   └── release/packageReleaseResources/out/
│       │   │   ├── desugar_graph/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│       │   │   │   ├── currentProject/
│       │   │   │   │   ├── jar_ef59e657812b2ebd2f8614d200c5c44775172adaa856ea8911d9ce7ca7c4f143_bucket_0/
│       │   │   │   │   │   └── graph.bin
│       │   │   │   │   ├── jar_ef59e657812b2ebd2f8614d200c5c44775172adaa856ea8911d9ce7ca7c4f143_bucket_1/
│       │   │   │   │   │   └── graph.bin
│       │   │   │   │   ├── jar_ef59e657812b2ebd2f8614d200c5c44775172adaa856ea8911d9ce7ca7c4f143_bucket_2/
│       │   │   │   │   │   └── graph.bin
│       │   │   │   │   ├── jar_ef59e657812b2ebd2f8614d200c5c44775172adaa856ea8911d9ce7ca7c4f143_bucket_3/
│       │   │   │   │   │   └── graph.bin
│       │   │   │   │   ├── jar_ef59e657812b2ebd2f8614d200c5c44775172adaa856ea8911d9ce7ca7c4f143_bucket_4/
│       │   │   │   │   │   └── graph.bin
│       │   │   │   │   └── jar_ef59e657812b2ebd2f8614d200c5c44775172adaa856ea8911d9ce7ca7c4f143_bucket_5/
│       │   │   │   │       └── graph.bin
│       │   │   │   ├── externalLibs/
│       │   │   │   ├── mixedScopes/
│       │   │   │   └── otherProjects/
│       │   │   ├── dex/debugAndroidTest/
│       │   │   │   ├── mergeExtDexDebugAndroidTest/
│       │   │   │   │   └── classes.dex
│       │   │   │   ├── mergeLibDexDebugAndroidTest/
│       │   │   │   │   ├── 0/
│       │   │   │   │   ├── 1/
│       │   │   │   │   ├── 10/
│       │   │   │   │   ├── 11/
│       │   │   │   │   ├── 12/
│       │   │   │   │   ├── 13/
│       │   │   │   │   ├── 14/
│       │   │   │   │   ├── 15/
│       │   │   │   │   ├── 2/
│       │   │   │   │   ├── 3/
│       │   │   │   │   ├── 4/
│       │   │   │   │   ├── 5/
│       │   │   │   │   ├── 6/
│       │   │   │   │   ├── 7/
│       │   │   │   │   │   └── classes.dex
│       │   │   │   │   ├── 8/
│       │   │   │   │   └── 9/
│       │   │   │   └── mergeProjectDexDebugAndroidTest/
│       │   │   │       ├── 0/
│       │   │   │       │   └── classes.dex
│       │   │   │       ├── 1/
│       │   │   │       ├── 10/
│       │   │   │       ├── 11/
│       │   │   │       ├── 12/
│       │   │   │       ├── 13/
│       │   │   │       ├── 14/
│       │   │   │       ├── 15/
│       │   │   │       ├── 2/
│       │   │   │       ├── 3/
│       │   │   │       ├── 4/
│       │   │   │       ├── 5/
│       │   │   │       ├── 6/
│       │   │   │       ├── 7/
│       │   │   │       ├── 8/
│       │   │   │       └── 9/
│       │   │   ├── dex_archive_input_jar_hashes/debugAndroidTest/dexBuilderDebugAndroidTest/
│       │   │   │   └── out
│       │   │   ├── dex_number_of_buckets_file/debugAndroidTest/dexBuilderDebugAndroidTest/
│       │   │   │   └── out
│       │   │   ├── duplicate_classes_check/debugAndroidTest/checkDebugAndroidTestDuplicateClasses/
│       │   │   ├── external_file_lib_dex_archives/debugAndroidTest/desugarDebugAndroidTestFileDependencies/
│       │   │   ├── external_libs_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│       │   │   ├── external_libs_dex_archive_with_artifact_transforms/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│       │   │   ├── generated_proguard_file/
│       │   │   │   ├── debug/mergeDebugGeneratedProguardFiles/
│       │   │   │   └── release/mergeReleaseGeneratedProguardFiles/
│       │   │   ├── incremental/
│       │   │   │   ├── debug/packageDebugResources/
│       │   │   │   │   ├── merged.dir/
│       │   │   │   │   ├── stripped.dir/
│       │   │   │   │   │       │   │   │   │   └── merger.xml
│       │   │   │   ├── debug-mergeJavaRes/
│       │   │   │   │   ├── zip-cache/
│       │   │   │   │   └── merge-state
│       │   │   │   ├── debugAndroidTest/
│       │   │   │   │   ├── mergeDebugAndroidTestResources/
│       │   │   │   │   │   ├── merged.dir/
│       │   │   │   │   │   │   ├── values/
│       │   │   │   │   │   │   │   └── values.xml
│       │   │   │   │   │   │   ├── values-af/
│       │   │   │   │   │   │   │   └── values-af.xml
│       │   │   │   │   │   │   ├── values-am/
│       │   │   │   │   │   │   │   └── values-am.xml
│       │   │   │   │   │   │   ├── values-ar/
│       │   │   │   │   │   │   │   └── values-ar.xml
│       │   │   │   │   │   │   ├── values-as/
│       │   │   │   │   │   │   │   └── values-as.xml
│       │   │   │   │   │   │   ├── values-az/
│       │   │   │   │   │   │   │   └── values-az.xml
│       │   │   │   │   │   │   ├── values-b+sr+Latn/
│       │   │   │   │   │   │   │   └── values-b+sr+Latn.xml
│       │   │   │   │   │   │   ├── values-be/
│       │   │   │   │   │   │   │   └── values-be.xml
│       │   │   │   │   │   │   ├── values-bg/
│       │   │   │   │   │   │   │   └── values-bg.xml
│       │   │   │   │   │   │   ├── values-bn/
│       │   │   │   │   │   │   │   └── values-bn.xml
│       │   │   │   │   │   │   ├── values-bs/
│       │   │   │   │   │   │   │   └── values-bs.xml
│       │   │   │   │   │   │   ├── values-ca/
│       │   │   │   │   │   │   │   └── values-ca.xml
│       │   │   │   │   │   │   ├── values-cs/
│       │   │   │   │   │   │   │   └── values-cs.xml
│       │   │   │   │   │   │   ├── values-da/
│       │   │   │   │   │   │   │   └── values-da.xml
│       │   │   │   │   │   │   ├── values-de/
│       │   │   │   │   │   │   │   └── values-de.xml
│       │   │   │   │   │   │   ├── values-el/
│       │   │   │   │   │   │   │   └── values-el.xml
│       │   │   │   │   │   │   ├── values-en-rAU/
│       │   │   │   │   │   │   │   └── values-en-rAU.xml
│       │   │   │   │   │   │   ├── values-en-rCA/
│       │   │   │   │   │   │   │   └── values-en-rCA.xml
│       │   │   │   │   │   │   ├── values-en-rGB/
│       │   │   │   │   │   │   │   └── values-en-rGB.xml
│       │   │   │   │   │   │   ├── values-en-rIN/
│       │   │   │   │   │   │   │   └── values-en-rIN.xml
│       │   │   │   │   │   │   ├── values-en-rXC/
│       │   │   │   │   │   │   │   └── values-en-rXC.xml
│       │   │   │   │   │   │   ├── values-es/
│       │   │   │   │   │   │   │   └── values-es.xml
│       │   │   │   │   │   │   ├── values-es-rUS/
│       │   │   │   │   │   │   │   └── values-es-rUS.xml
│       │   │   │   │   │   │   ├── values-et/
│       │   │   │   │   │   │   │   └── values-et.xml
│       │   │   │   │   │   │   ├── values-eu/
│       │   │   │   │   │   │   │   └── values-eu.xml
│       │   │   │   │   │   │   ├── values-fa/
│       │   │   │   │   │   │   │   └── values-fa.xml
│       │   │   │   │   │   │   ├── values-fi/
│       │   │   │   │   │   │   │   └── values-fi.xml
│       │   │   │   │   │   │   ├── values-fr/
│       │   │   │   │   │   │   │   └── values-fr.xml
│       │   │   │   │   │   │   ├── values-fr-rCA/
│       │   │   │   │   │   │   │   └── values-fr-rCA.xml
│       │   │   │   │   │   │   ├── values-gl/
│       │   │   │   │   │   │   │   └── values-gl.xml
│       │   │   │   │   │   │   ├── values-gu/
│       │   │   │   │   │   │   │   └── values-gu.xml
│       │   │   │   │   │   │   ├── values-hi/
│       │   │   │   │   │   │   │   └── values-hi.xml
│       │   │   │   │   │   │   ├── values-hr/
│       │   │   │   │   │   │   │   └── values-hr.xml
│       │   │   │   │   │   │   ├── values-hu/
│       │   │   │   │   │   │   │   └── values-hu.xml
│       │   │   │   │   │   │   ├── values-hy/
│       │   │   │   │   │   │   │   └── values-hy.xml
│       │   │   │   │   │   │   ├── values-in/
│       │   │   │   │   │   │   │   └── values-in.xml
│       │   │   │   │   │   │   ├── values-is/
│       │   │   │   │   │   │   │   └── values-is.xml
│       │   │   │   │   │   │   ├── values-it/
│       │   │   │   │   │   │   │   └── values-it.xml
│       │   │   │   │   │   │   ├── values-iw/
│       │   │   │   │   │   │   │   └── values-iw.xml
│       │   │   │   │   │   │   ├── values-ja/
│       │   │   │   │   │   │   │   └── values-ja.xml
│       │   │   │   │   │   │   ├── values-ka/
│       │   │   │   │   │   │   │   └── values-ka.xml
│       │   │   │   │   │   │   ├── values-kk/
│       │   │   │   │   │   │   │   └── values-kk.xml
│       │   │   │   │   │   │   ├── values-km/
│       │   │   │   │   │   │   │   └── values-km.xml
│       │   │   │   │   │   │   ├── values-kn/
│       │   │   │   │   │   │   │   └── values-kn.xml
│       │   │   │   │   │   │   ├── values-ko/
│       │   │   │   │   │   │   │   └── values-ko.xml
│       │   │   │   │   │   │   ├── values-ky/
│       │   │   │   │   │   │   │   └── values-ky.xml
│       │   │   │   │   │   │   ├── values-lo/
│       │   │   │   │   │   │   │   └── values-lo.xml
│       │   │   │   │   │   │   ├── values-lt/
│       │   │   │   │   │   │   │   └── values-lt.xml
│       │   │   │   │   │   │   ├── values-lv/
│       │   │   │   │   │   │   │   └── values-lv.xml
│       │   │   │   │   │   │   ├── values-mk/
│       │   │   │   │   │   │   │   └── values-mk.xml
│       │   │   │   │   │   │   ├── values-ml/
│       │   │   │   │   │   │   │   └── values-ml.xml
│       │   │   │   │   │   │   ├── values-mn/
│       │   │   │   │   │   │   │   └── values-mn.xml
│       │   │   │   │   │   │   ├── values-mr/
│       │   │   │   │   │   │   │   └── values-mr.xml
│       │   │   │   │   │   │   ├── values-ms/
│       │   │   │   │   │   │   │   └── values-ms.xml
│       │   │   │   │   │   │   ├── values-my/
│       │   │   │   │   │   │   │   └── values-my.xml
│       │   │   │   │   │   │   ├── values-nb/
│       │   │   │   │   │   │   │   └── values-nb.xml
│       │   │   │   │   │   │   ├── values-ne/
│       │   │   │   │   │   │   │   └── values-ne.xml
│       │   │   │   │   │   │   ├── values-nl/
│       │   │   │   │   │   │   │   └── values-nl.xml
│       │   │   │   │   │   │   ├── values-or/
│       │   │   │   │   │   │   │   └── values-or.xml
│       │   │   │   │   │   │   ├── values-pa/
│       │   │   │   │   │   │   │   └── values-pa.xml
│       │   │   │   │   │   │   ├── values-pl/
│       │   │   │   │   │   │   │   └── values-pl.xml
│       │   │   │   │   │   │   ├── values-pt/
│       │   │   │   │   │   │   │   └── values-pt.xml
│       │   │   │   │   │   │   ├── values-pt-rBR/
│       │   │   │   │   │   │   │   └── values-pt-rBR.xml
│       │   │   │   │   │   │   ├── values-pt-rPT/
│       │   │   │   │   │   │   │   └── values-pt-rPT.xml
│       │   │   │   │   │   │   ├── values-ro/
│       │   │   │   │   │   │   │   └── values-ro.xml
│       │   │   │   │   │   │   ├── values-ru/
│       │   │   │   │   │   │   │   └── values-ru.xml
│       │   │   │   │   │   │   ├── values-si/
│       │   │   │   │   │   │   │   └── values-si.xml
│       │   │   │   │   │   │   ├── values-sk/
│       │   │   │   │   │   │   │   └── values-sk.xml
│       │   │   │   │   │   │   ├── values-sl/
│       │   │   │   │   │   │   │   └── values-sl.xml
│       │   │   │   │   │   │   ├── values-sq/
│       │   │   │   │   │   │   │   └── values-sq.xml
│       │   │   │   │   │   │   ├── values-sr/
│       │   │   │   │   │   │   │   └── values-sr.xml
│       │   │   │   │   │   │   ├── values-sv/
│       │   │   │   │   │   │   │   └── values-sv.xml
│       │   │   │   │   │   │   ├── values-sw/
│       │   │   │   │   │   │   │   └── values-sw.xml
│       │   │   │   │   │   │   ├── values-ta/
│       │   │   │   │   │   │   │   └── values-ta.xml
│       │   │   │   │   │   │   ├── values-te/
│       │   │   │   │   │   │   │   └── values-te.xml
│       │   │   │   │   │   │   ├── values-th/
│       │   │   │   │   │   │   │   └── values-th.xml
│       │   │   │   │   │   │   ├── values-tl/
│       │   │   │   │   │   │   │   └── values-tl.xml
│       │   │   │   │   │   │   ├── values-tr/
│       │   │   │   │   │   │   │   └── values-tr.xml
│       │   │   │   │   │   │   ├── values-uk/
│       │   │   │   │   │   │   │   └── values-uk.xml
│       │   │   │   │   │   │   ├── values-ur/
│       │   │   │   │   │   │   │   └── values-ur.xml
│       │   │   │   │   │   │   ├── values-uz/
│       │   │   │   │   │   │   │   └── values-uz.xml
│       │   │   │   │   │   │   ├── values-v21/
│       │   │   │   │   │   │   │   └── values-v21.xml
│       │   │   │   │   │   │   ├── values-vi/
│       │   │   │   │   │   │   │   └── values-vi.xml
│       │   │   │   │   │   │   ├── values-zh-rCN/
│       │   │   │   │   │   │   │   └── values-zh-rCN.xml
│       │   │   │   │   │   │   ├── values-zh-rHK/
│       │   │   │   │   │   │   │   └── values-zh-rHK.xml
│       │   │   │   │   │   │   ├── values-zh-rTW/
│       │   │   │   │   │   │   │   └── values-zh-rTW.xml
│       │   │   │   │   │   │   └── values-zu/
│       │   │   │   │   │   │       └── values-zu.xml
│       │   │   │   │   │   ├── stripped.dir/
│       │   │   │   │   │   │       │   │   │   │   │   └── merger.xml
│       │   │   │   │   └── packageDebugAndroidTestResources/
│       │   │   │   │       ├── merged.dir/
│       │   │   │   │       ├── stripped.dir/
│       │   │   │   │       │       │   │   │   │       └── merger.xml
│       │   │   │   ├── debugAndroidTest-mergeJavaRes/
│       │   │   │   │   ├── zip-cache/
│       │   │   │   │   │   ├── 1ZuCeLYWmYw5MfiXYYnDIw==
│       │   │   │   │   │   ├── 3VJ0q7BGqqlcLs7MI_dHlw==
│       │   │   │   │   │   ├── 7trlVWptO4XEuV+Pwn+FJg==
│       │   │   │   │   │   ├── _hdAXZS0wZVQGsNJYelpuw==
│       │   │   │   │   │   ├── Aum8RyvyKLwxGsZvF8wZew==
│       │   │   │   │   │   ├── B081BuIDaWSo5McNdeDOlg==
│       │   │   │   │   │   ├── BIO8B+IR6k405DzX3KVLfg==
│       │   │   │   │   │   ├── eAb6b17AoioR+wuvsUlXUg==
│       │   │   │   │   │   ├── EKfNJDNp1qej0g_hpuBvyw==
│       │   │   │   │   │   ├── fbJN1jTpNAQTBDjunGpNNw==
│       │   │   │   │   │   ├── gMUj5N1rwbVTZNnhBOwS4w==
│       │   │   │   │   │   ├── i1fywGgR7739mcd2F7zSGg==
│       │   │   │   │   │   ├── IO63ZVBDFar17KQf_eKhoQ==
│       │   │   │   │   │   ├── J7CAnpdxVb_3lbUfaCbsMg==
│       │   │   │   │   │   ├── J7iYjow924XXI0QA2R4XxA==
│       │   │   │   │   │   ├── jrJKw3qGpVbyDMBUg69i1A==
│       │   │   │   │   │   ├── kBPjyU0m7mBApb7pIiwI2Q==
│       │   │   │   │   │   ├── Kws7Ph2jc1f3d_ecLLw8yA==
│       │   │   │   │   │   ├── L+aTIiZM60Y12ESzMr+bBQ==
│       │   │   │   │   │   ├── m7ifgdNNq+vMZDPtmarc1Q==
│       │   │   │   │   │   ├── MGjkoJZVhVbQnzYuPyFT2g==
│       │   │   │   │   │   ├── MoWJlOGWBfjVRC8RvC2PxA==
│       │   │   │   │   │   ├── N4JbPtX00mbgf1JBj8hLpA==
│       │   │   │   │   │   ├── qD5dgASdHa8TAOro2Bvrtw==
│       │   │   │   │   │   ├── qOLoLN95hFQC6sku5Qy+CQ==
│       │   │   │   │   │   ├── rrna0K8qZsqmJGvstjZKCQ==
│       │   │   │   │   │   ├── tL2pp5i9_KZCC65aeIVhDg==
│       │   │   │   │   │   ├── v1FFNCoEO5or6_tvb1ZiNA==
│       │   │   │   │   │   └── V8DxNbbYWglX3HsdJ5bXKg==
│       │   │   │   │   └── merge-state
│       │   │   │   ├── mergeDebugAndroidTestAssets/
│       │   │   │   │   └── merger.xml
│       │   │   │   ├── mergeDebugAndroidTestJniLibFolders/
│       │   │   │   │   └── merger.xml
│       │   │   │   ├── mergeDebugAssets/
│       │   │   │   │   └── merger.xml
│       │   │   │   ├── mergeDebugJniLibFolders/
│       │   │   │   │   └── merger.xml
│       │   │   │   ├── mergeReleaseAssets/
│       │   │   │   │   └── merger.xml
│       │   │   │   ├── mergeReleaseJniLibFolders/
│       │   │   │   │   └── merger.xml
│       │   │   │   ├── packageDebugAndroidTest/tmp/debugAndroidTest/
│       │   │   │   │   ├── zip-cache/
│       │   │   │   │   │   ├── androidResources
│       │   │   │   │   │   └── javaResources0
│       │   │   │   │   └── dex-renamer-state.txt
│       │   │   │   └── release/packageReleaseResources/
│       │   │   │       ├── merged.dir/
│       │   │   │       ├── stripped.dir/
│       │   │   │       │       │   │   │       └── merger.xml
│       │   │   ├── java_res/
│       │   │   │   ├── debug/processDebugJavaRes/out/
│       │   │   │   │   ├── com/miguelrodriguez19/safecube/feature/vault/navigation/
│       │   │   │   │   └── META-INF/
│       │   │   │   │       └── vault.kotlin_module
│       │   │   │   └── release/processReleaseJavaRes/out/
│       │   │   │       ├── com/miguelrodriguez19/safecube/feature/vault/navigation/
│       │   │   │       └── META-INF/
│       │   │   │           └── vault.kotlin_module
│       │   │   ├── library_and_local_jars_jni/debug/copyDebugJniLibsProjectAndLocalJars/jni/
│       │   │   ├── library_art_profile/
│       │   │   │   ├── debug/prepareDebugArtProfile/
│       │   │   │   └── release/prepareReleaseArtProfile/
│       │   │   ├── library_jni/
│       │   │   │   ├── debug/copyDebugJniLibsProjectOnly/jni/
│       │   │   │   └── release/copyReleaseJniLibsProjectOnly/jni/
│       │   │   ├── linked_resources_binary_format/debugAndroidTest/processDebugAndroidTestResources/
│       │   │   │   ├── linked-resources-binary-format.ap_
│       │   │   │   └── output-metadata.json
│       │   │   ├── lint_publish_jar/global/prepareLintJarForPublish/
│       │   │   ├── local_only_symbol_list/
│       │   │   │   ├── debug/parseDebugLocalResources/
│       │   │   │   │   └── R-def.txt
│       │   │   │   ├── debugAndroidTest/parseDebugAndroidTestLocalResources/
│       │   │   │   │   └── R-def.txt
│       │   │   │   └── release/parseReleaseLocalResources/
│       │   │   │       └── R-def.txt
│       │   │   ├── manifest_merge_blame_file/
│       │   │   │   ├── debug/processDebugManifest/
│       │   │   │   │   └── manifest-merger-blame-debug-report.txt
│       │   │   │   ├── debugAndroidTest/processDebugAndroidTestManifest/
│       │   │   │   │   └── manifest-merger-blame-debug-androidTest-report.txt
│       │   │   │   └── release/processReleaseManifest/
│       │   │   │       └── manifest-merger-blame-release-report.txt
│       │   │   ├── merged_consumer_proguard_file/debug/mergeDebugConsumerProguardFiles/
│       │   │   ├── merged_java_res/
│       │   │   │   ├── debug/mergeDebugJavaResource/
│       │   │   │   │   └── feature-vault.jar
│       │   │   │   └── debugAndroidTest/mergeDebugAndroidTestJavaResource/
│       │   │   │       └── feature-vault.jar
│       │   │   ├── merged_jni_libs/
│       │   │   │   ├── debug/mergeDebugJniLibFolders/out/
│       │   │   │   ├── debugAndroidTest/mergeDebugAndroidTestJniLibFolders/out/
│       │   │   │   └── release/mergeReleaseJniLibFolders/out/
│       │   │   ├── merged_manifest/
│       │   │   │   ├── debug/processDebugManifest/
│       │   │   │   │   └── AndroidManifest.xml
│       │   │   │   └── release/processReleaseManifest/
│       │   │   │       └── AndroidManifest.xml
│       │   │   ├── merged_res/debugAndroidTest/mergeDebugAndroidTestResources/
│       │   │   │   ├── values-af_values-af.arsc.flat
│       │   │   │   ├── values-am_values-am.arsc.flat
│       │   │   │   ├── values-ar_values-ar.arsc.flat
│       │   │   │   ├── values-as_values-as.arsc.flat
│       │   │   │   ├── values-az_values-az.arsc.flat
│       │   │   │   ├── values-b+sr+Latn_values-b+sr+Latn.arsc.flat
│       │   │   │   ├── values-be_values-be.arsc.flat
│       │   │   │   ├── values-bg_values-bg.arsc.flat
│       │   │   │   ├── values-bn_values-bn.arsc.flat
│       │   │   │   ├── values-bs_values-bs.arsc.flat
│       │   │   │   ├── values-ca_values-ca.arsc.flat
│       │   │   │   ├── values-cs_values-cs.arsc.flat
│       │   │   │   ├── values-da_values-da.arsc.flat
│       │   │   │   ├── values-de_values-de.arsc.flat
│       │   │   │   ├── values-el_values-el.arsc.flat
│       │   │   │   ├── values-en-rAU_values-en-rAU.arsc.flat
│       │   │   │   ├── values-en-rCA_values-en-rCA.arsc.flat
│       │   │   │   ├── values-en-rGB_values-en-rGB.arsc.flat
│       │   │   │   ├── values-en-rIN_values-en-rIN.arsc.flat
│       │   │   │   ├── values-en-rXC_values-en-rXC.arsc.flat
│       │   │   │   ├── values-es-rUS_values-es-rUS.arsc.flat
│       │   │   │   ├── values-es_values-es.arsc.flat
│       │   │   │   ├── values-et_values-et.arsc.flat
│       │   │   │   ├── values-eu_values-eu.arsc.flat
│       │   │   │   ├── values-fa_values-fa.arsc.flat
│       │   │   │   ├── values-fi_values-fi.arsc.flat
│       │   │   │   ├── values-fr-rCA_values-fr-rCA.arsc.flat
│       │   │   │   ├── values-fr_values-fr.arsc.flat
│       │   │   │   ├── values-gl_values-gl.arsc.flat
│       │   │   │   ├── values-gu_values-gu.arsc.flat
│       │   │   │   ├── values-hi_values-hi.arsc.flat
│       │   │   │   ├── values-hr_values-hr.arsc.flat
│       │   │   │   ├── values-hu_values-hu.arsc.flat
│       │   │   │   ├── values-hy_values-hy.arsc.flat
│       │   │   │   ├── values-in_values-in.arsc.flat
│       │   │   │   ├── values-is_values-is.arsc.flat
│       │   │   │   ├── values-it_values-it.arsc.flat
│       │   │   │   ├── values-iw_values-iw.arsc.flat
│       │   │   │   ├── values-ja_values-ja.arsc.flat
│       │   │   │   ├── values-ka_values-ka.arsc.flat
│       │   │   │   ├── values-kk_values-kk.arsc.flat
│       │   │   │   ├── values-km_values-km.arsc.flat
│       │   │   │   ├── values-kn_values-kn.arsc.flat
│       │   │   │   ├── values-ko_values-ko.arsc.flat
│       │   │   │   ├── values-ky_values-ky.arsc.flat
│       │   │   │   ├── values-lo_values-lo.arsc.flat
│       │   │   │   ├── values-lt_values-lt.arsc.flat
│       │   │   │   ├── values-lv_values-lv.arsc.flat
│       │   │   │   ├── values-mk_values-mk.arsc.flat
│       │   │   │   ├── values-ml_values-ml.arsc.flat
│       │   │   │   ├── values-mn_values-mn.arsc.flat
│       │   │   │   ├── values-mr_values-mr.arsc.flat
│       │   │   │   ├── values-ms_values-ms.arsc.flat
│       │   │   │   ├── values-my_values-my.arsc.flat
│       │   │   │   ├── values-nb_values-nb.arsc.flat
│       │   │   │   ├── values-ne_values-ne.arsc.flat
│       │   │   │   ├── values-nl_values-nl.arsc.flat
│       │   │   │   ├── values-or_values-or.arsc.flat
│       │   │   │   ├── values-pa_values-pa.arsc.flat
│       │   │   │   ├── values-pl_values-pl.arsc.flat
│       │   │   │   ├── values-pt-rBR_values-pt-rBR.arsc.flat
│       │   │   │   ├── values-pt-rPT_values-pt-rPT.arsc.flat
│       │   │   │   ├── values-pt_values-pt.arsc.flat
│       │   │   │   ├── values-ro_values-ro.arsc.flat
│       │   │   │   ├── values-ru_values-ru.arsc.flat
│       │   │   │   ├── values-si_values-si.arsc.flat
│       │   │   │   ├── values-sk_values-sk.arsc.flat
│       │   │   │   ├── values-sl_values-sl.arsc.flat
│       │   │   │   ├── values-sq_values-sq.arsc.flat
│       │   │   │   ├── values-sr_values-sr.arsc.flat
│       │   │   │   ├── values-sv_values-sv.arsc.flat
│       │   │   │   ├── values-sw_values-sw.arsc.flat
│       │   │   │   ├── values-ta_values-ta.arsc.flat
│       │   │   │   ├── values-te_values-te.arsc.flat
│       │   │   │   ├── values-th_values-th.arsc.flat
│       │   │   │   ├── values-tl_values-tl.arsc.flat
│       │   │   │   ├── values-tr_values-tr.arsc.flat
│       │   │   │   ├── values-uk_values-uk.arsc.flat
│       │   │   │   ├── values-ur_values-ur.arsc.flat
│       │   │   │   ├── values-uz_values-uz.arsc.flat
│       │   │   │   ├── values-v21_values-v21.arsc.flat
│       │   │   │   ├── values-vi_values-vi.arsc.flat
│       │   │   │   ├── values-zh-rCN_values-zh-rCN.arsc.flat
│       │   │   │   ├── values-zh-rHK_values-zh-rHK.arsc.flat
│       │   │   │   ├── values-zh-rTW_values-zh-rTW.arsc.flat
│       │   │   │   ├── values-zu_values-zu.arsc.flat
│       │   │   │   └── values_values.arsc.flat
│       │   │   ├── merged_res_blame_folder/debugAndroidTest/mergeDebugAndroidTestResources/out/multi-v2/
│       │   │   │   ├── mergeDebugAndroidTestResources.json
│       │   │   │   ├── values-af.json
│       │   │   │   ├── values-am.json
│       │   │   │   ├── values-ar.json
│       │   │   │   ├── values-as.json
│       │   │   │   ├── values-az.json
│       │   │   │   ├── values-b+sr+Latn.json
│       │   │   │   ├── values-be.json
│       │   │   │   ├── values-bg.json
│       │   │   │   ├── values-bn.json
│       │   │   │   ├── values-bs.json
│       │   │   │   ├── values-ca.json
│       │   │   │   ├── values-cs.json
│       │   │   │   ├── values-da.json
│       │   │   │   ├── values-de.json
│       │   │   │   ├── values-el.json
│       │   │   │   ├── values-en-rAU.json
│       │   │   │   ├── values-en-rCA.json
│       │   │   │   ├── values-en-rGB.json
│       │   │   │   ├── values-en-rIN.json
│       │   │   │   ├── values-en-rXC.json
│       │   │   │   ├── values-es-rUS.json
│       │   │   │   ├── values-es.json
│       │   │   │   ├── values-et.json
│       │   │   │   ├── values-eu.json
│       │   │   │   ├── values-fa.json
│       │   │   │   ├── values-fi.json
│       │   │   │   ├── values-fr-rCA.json
│       │   │   │   ├── values-fr.json
│       │   │   │   ├── values-gl.json
│       │   │   │   ├── values-gu.json
│       │   │   │   ├── values-hi.json
│       │   │   │   ├── values-hr.json
│       │   │   │   ├── values-hu.json
│       │   │   │   ├── values-hy.json
│       │   │   │   ├── values-in.json
│       │   │   │   ├── values-is.json
│       │   │   │   ├── values-it.json
│       │   │   │   ├── values-iw.json
│       │   │   │   ├── values-ja.json
│       │   │   │   ├── values-ka.json
│       │   │   │   ├── values-kk.json
│       │   │   │   ├── values-km.json
│       │   │   │   ├── values-kn.json
│       │   │   │   ├── values-ko.json
│       │   │   │   ├── values-ky.json
│       │   │   │   ├── values-lo.json
│       │   │   │   ├── values-lt.json
│       │   │   │   ├── values-lv.json
│       │   │   │   ├── values-mk.json
│       │   │   │   ├── values-ml.json
│       │   │   │   ├── values-mn.json
│       │   │   │   ├── values-mr.json
│       │   │   │   ├── values-ms.json
│       │   │   │   ├── values-my.json
│       │   │   │   ├── values-nb.json
│       │   │   │   ├── values-ne.json
│       │   │   │   ├── values-nl.json
│       │   │   │   ├── values-or.json
│       │   │   │   ├── values-pa.json
│       │   │   │   ├── values-pl.json
│       │   │   │   ├── values-pt-rBR.json
│       │   │   │   ├── values-pt-rPT.json
│       │   │   │   ├── values-pt.json
│       │   │   │   ├── values-ro.json
│       │   │   │   ├── values-ru.json
│       │   │   │   ├── values-si.json
│       │   │   │   ├── values-sk.json
│       │   │   │   ├── values-sl.json
│       │   │   │   ├── values-sq.json
│       │   │   │   ├── values-sr.json
│       │   │   │   ├── values-sv.json
│       │   │   │   ├── values-sw.json
│       │   │   │   ├── values-ta.json
│       │   │   │   ├── values-te.json
│       │   │   │   ├── values-th.json
│       │   │   │   ├── values-tl.json
│       │   │   │   ├── values-tr.json
│       │   │   │   ├── values-uk.json
│       │   │   │   ├── values-ur.json
│       │   │   │   ├── values-uz.json
│       │   │   │   ├── values-v21.json
│       │   │   │   ├── values-vi.json
│       │   │   │   ├── values-zh-rCN.json
│       │   │   │   ├── values-zh-rHK.json
│       │   │   │   ├── values-zh-rTW.json
│       │   │   │   ├── values-zu.json
│       │   │   │   └── values.json
│       │   │   ├── mixed_scope_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│       │   │   ├── navigation_json/
│       │   │   │   ├── debug/extractDeepLinksDebug/
│       │   │   │   │   └── navigation.json
│       │   │   │   └── release/extractDeepLinksRelease/
│       │   │   │       └── navigation.json
│       │   │   ├── navigation_json_for_aar/debug/extractDeepLinksForAarDebug/
│       │   │   ├── nested_resources_validation_report/
│       │   │   │   ├── debug/generateDebugResources/
│       │   │   │   │   └── nestedResourcesValidationReport.txt
│       │   │   │   ├── debugAndroidTest/generateDebugAndroidTestResources/
│       │   │   │   │   └── nestedResourcesValidationReport.txt
│       │   │   │   └── release/generateReleaseResources/
│       │   │   │       └── nestedResourcesValidationReport.txt
│       │   │   ├── packaged_manifests/debugAndroidTest/processDebugAndroidTestManifest/
│       │   │   │   ├── AndroidManifest.xml
│       │   │   │   └── output-metadata.json
│       │   │   ├── packaged_res/
│       │   │   │   ├── debug/packageDebugResources/
│       │   │   │   ├── debugAndroidTest/packageDebugAndroidTestResources/
│       │   │   │   └── release/packageReleaseResources/
│       │   │   ├── project_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│       │   │   │   ├── 078d85e5e99a3fb7788c1bf61213afbd4a07abc55225d39fef274961b68c723e_0.jar
│       │   │   │   ├── 078d85e5e99a3fb7788c1bf61213afbd4a07abc55225d39fef274961b68c723e_1.jar
│       │   │   │   ├── 078d85e5e99a3fb7788c1bf61213afbd4a07abc55225d39fef274961b68c723e_2.jar
│       │   │   │   ├── 078d85e5e99a3fb7788c1bf61213afbd4a07abc55225d39fef274961b68c723e_3.jar
│       │   │   │   ├── 078d85e5e99a3fb7788c1bf61213afbd4a07abc55225d39fef274961b68c723e_4.jar
│       │   │   │   └── 078d85e5e99a3fb7788c1bf61213afbd4a07abc55225d39fef274961b68c723e_5.jar
│       │   │   ├── public_res/
│       │   │   │   ├── debug/packageDebugResources/
│       │   │   │   └── release/packageReleaseResources/
│       │   │   ├── runtime_library_classes_dir/debug/bundleLibRuntimeToDirDebug/
│       │   │   │   ├── com/miguelrodriguez19/safecube/feature/vault/navigation/
│       │   │   │   │   ├── AppTab.class
│       │   │   │   │   ├── ComposableSingletons$CreateVaultScreenKt.class
│       │   │   │   │   ├── ComposableSingletons$NavigationBarKt.class
│       │   │   │   │   ├── ComposableSingletons$RecoveryKeyScreenKt.class
│       │   │   │   │   ├── ComposableSingletons$SettingsScreenKt.class
│       │   │   │   │   ├── ComposableSingletons$UnlockVaultScreenKt.class
│       │   │   │   │   ├── ComposableSingletons$VaultFoldersScreenKt.class
│       │   │   │   │   ├── ComposableSingletons$VaultScreenKt.class
│       │   │   │   │   ├── CreateVaultScreenKt.class
│       │   │   │   │   ├── NavigationBarKt.class
│       │   │   │   │   ├── RecoveryKeyScreenKt.class
│       │   │   │   │   ├── SettingsScreenKt.class
│       │   │   │   │   ├── UnlockVaultScreenKt.class
│       │   │   │   │   ├── VaultFoldersScreenKt.class
│       │   │   │   │   └── VaultScreenKt.class
│       │   │   │   └── META-INF/
│       │   │   │       └── vault.kotlin_module
│       │   │   ├── runtime_library_classes_jar/
│       │   │   │   ├── debug/bundleLibRuntimeToJarDebug/
│       │   │   │   │   └── classes.jar
│       │   │   │   └── release/bundleLibRuntimeToJarRelease/
│       │   │   │       └── classes.jar
│       │   │   ├── runtime_symbol_list/debugAndroidTest/processDebugAndroidTestResources/
│       │   │   │   └── R.txt
│       │   │   ├── signing_config_versions/debugAndroidTest/writeDebugAndroidTestSigningConfigVersions/
│       │   │   │   └── signing-config-versions.json
│       │   │   ├── stable_resource_ids_file/debugAndroidTest/processDebugAndroidTestResources/
│       │   │   │   └── stableIds.txt
│       │   │   ├── sub_project_dex_archive/debugAndroidTest/dexBuilderDebugAndroidTest/out/
│       │   │   ├── symbol_list_with_package_name/
│       │   │   │   ├── debug/generateDebugRFile/
│       │   │   │   │   └── package-aware-r.txt
│       │   │   │   ├── debugAndroidTest/generateDebugAndroidTestRFile/
│       │   │   │   │   └── package-aware-r.txt
│       │   │   │   └── release/generateReleaseRFile/
│       │   │   │       └── package-aware-r.txt
│       │   │   ├── tmp/manifest/androidTest/debug/
│       │   │   └── validate_signing_config/debugAndroidTest/validateSigningDebugAndroidTest/
│       │   ├── kotlin/
│       │   │   ├── compileDebugKotlin/
│       │   │   │   ├── cacheable/
│       │   │   │   │   ├── caches-jvm/
│       │   │   │   │   │   ├── compilerPluginFiles/
│       │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab
│       │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│       │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│       │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.len
│       │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│       │   │   │   │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│       │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab
│       │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│       │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│       │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.len
│       │   │   │   │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│       │   │   │   │   │   │   └── sources-referenced-by-plugins.tab_i.len
│       │   │   │   │   │   ├── inputs/
│       │   │   │   │   │   │   ├── source-to-output.tab
│       │   │   │   │   │   │   ├── source-to-output.tab.keystream
│       │   │   │   │   │   │   ├── source-to-output.tab.keystream.len
│       │   │   │   │   │   │   ├── source-to-output.tab.len
│       │   │   │   │   │   │   ├── source-to-output.tab.values.at
│       │   │   │   │   │   │   ├── source-to-output.tab_i
│       │   │   │   │   │   │   └── source-to-output.tab_i.len
│       │   │   │   │   │   ├── jvm/
│       │   │   │   │   │   │   └── kotlin/
│       │   │   │   │   │   │       ├── class-attributes.tab
│       │   │   │   │   │   │       ├── class-attributes.tab.keystream
│       │   │   │   │   │   │       ├── class-attributes.tab.keystream.len
│       │   │   │   │   │   │       ├── class-attributes.tab.len
│       │   │   │   │   │   │       ├── class-attributes.tab.values.at
│       │   │   │   │   │   │       ├── class-attributes.tab_i
│       │   │   │   │   │   │       ├── class-attributes.tab_i.len
│       │   │   │   │   │   │       ├── class-fq-name-to-source.tab
│       │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream
│       │   │   │   │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│       │   │   │   │   │   │       ├── class-fq-name-to-source.tab.len
│       │   │   │   │   │   │       ├── class-fq-name-to-source.tab.values.at
│       │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i
│       │   │   │   │   │   │       ├── class-fq-name-to-source.tab_i.len
│       │   │   │   │   │   │       ├── internal-name-to-source.tab
│       │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream
│       │   │   │   │   │   │       ├── internal-name-to-source.tab.keystream.len
│       │   │   │   │   │   │       ├── internal-name-to-source.tab.len
│       │   │   │   │   │   │       ├── internal-name-to-source.tab.values.at
│       │   │   │   │   │   │       ├── internal-name-to-source.tab_i
│       │   │   │   │   │   │       ├── internal-name-to-source.tab_i.len
│       │   │   │   │   │   │       ├── package-parts.tab
│       │   │   │   │   │   │       ├── package-parts.tab.keystream
│       │   │   │   │   │   │       ├── package-parts.tab.keystream.len
│       │   │   │   │   │   │       ├── package-parts.tab.len
│       │   │   │   │   │   │       ├── package-parts.tab.values.at
│       │   │   │   │   │   │       ├── package-parts.tab_i
│       │   │   │   │   │   │       ├── package-parts.tab_i.len
│       │   │   │   │   │   │       ├── proto.tab
│       │   │   │   │   │   │       ├── proto.tab.keystream
│       │   │   │   │   │   │       ├── proto.tab.keystream.len
│       │   │   │   │   │   │       ├── proto.tab.len
│       │   │   │   │   │   │       ├── proto.tab.values.at
│       │   │   │   │   │   │       ├── proto.tab_i
│       │   │   │   │   │   │       ├── proto.tab_i.len
│       │   │   │   │   │   │       ├── source-to-classes.tab
│       │   │   │   │   │   │       ├── source-to-classes.tab.keystream
│       │   │   │   │   │   │       ├── source-to-classes.tab.keystream.len
│       │   │   │   │   │   │       ├── source-to-classes.tab.len
│       │   │   │   │   │   │       ├── source-to-classes.tab.values.at
│       │   │   │   │   │   │       ├── source-to-classes.tab_i
│       │   │   │   │   │   │       ├── source-to-classes.tab_i.len
│       │   │   │   │   │   │       ├── subtypes.tab
│       │   │   │   │   │   │       ├── subtypes.tab.keystream
│       │   │   │   │   │   │       ├── subtypes.tab.keystream.len
│       │   │   │   │   │   │       ├── subtypes.tab.len
│       │   │   │   │   │   │       ├── subtypes.tab.values.at
│       │   │   │   │   │   │       ├── subtypes.tab_i
│       │   │   │   │   │   │       ├── subtypes.tab_i.len
│       │   │   │   │   │   │       ├── supertypes.tab
│       │   │   │   │   │   │       ├── supertypes.tab.keystream
│       │   │   │   │   │   │       ├── supertypes.tab.keystream.len
│       │   │   │   │   │   │       ├── supertypes.tab.len
│       │   │   │   │   │   │       ├── supertypes.tab.values.at
│       │   │   │   │   │   │       ├── supertypes.tab_i
│       │   │   │   │   │   │       └── supertypes.tab_i.len
│       │   │   │   │   │   └── lookups/
│       │   │   │   │   │       ├── counters.tab
│       │   │   │   │   │       ├── file-to-id.tab
│       │   │   │   │   │       ├── file-to-id.tab.keystream
│       │   │   │   │   │       ├── file-to-id.tab.keystream.len
│       │   │   │   │   │       ├── file-to-id.tab.len
│       │   │   │   │   │       ├── file-to-id.tab.values.at
│       │   │   │   │   │       ├── file-to-id.tab_i
│       │   │   │   │   │       ├── file-to-id.tab_i.len
│       │   │   │   │   │       ├── id-to-file.tab
│       │   │   │   │   │       ├── id-to-file.tab.keystream
│       │   │   │   │   │       ├── id-to-file.tab.keystream.len
│       │   │   │   │   │       ├── id-to-file.tab.len
│       │   │   │   │   │       ├── id-to-file.tab.values.at
│       │   │   │   │   │       ├── id-to-file.tab_i
│       │   │   │   │   │       ├── id-to-file.tab_i.len
│       │   │   │   │   │       ├── lookups.tab
│       │   │   │   │   │       ├── lookups.tab.keystream
│       │   │   │   │   │       ├── lookups.tab.keystream.len
│       │   │   │   │   │       ├── lookups.tab.len
│       │   │   │   │   │       ├── lookups.tab.values.at
│       │   │   │   │   │       ├── lookups.tab_i
│       │   │   │   │   │       └── lookups.tab_i.len
│       │   │   │   │   └── last-build.bin
│       │   │   │   ├── classpath-snapshot/
│       │   │   │   │   └── shrunk-classpath-snapshot.bin
│       │   │   │   └── local-state/
│       │   │   └── compileReleaseKotlin/
│       │   │       ├── cacheable/
│       │   │       │   ├── caches-jvm/
│       │   │       │   │   ├── compilerPluginFiles/
│       │   │       │   │   │   ├── outputs-generated-for-plugins.tab
│       │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream
│       │   │       │   │   │   ├── outputs-generated-for-plugins.tab.keystream.len
│       │   │       │   │   │   ├── outputs-generated-for-plugins.tab.len
│       │   │       │   │   │   ├── outputs-generated-for-plugins.tab.values.at
│       │   │       │   │   │   ├── outputs-generated-for-plugins.tab_i.len
│       │   │       │   │   │   ├── sources-referenced-by-plugins.tab
│       │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream
│       │   │       │   │   │   ├── sources-referenced-by-plugins.tab.keystream.len
│       │   │       │   │   │   ├── sources-referenced-by-plugins.tab.len
│       │   │       │   │   │   ├── sources-referenced-by-plugins.tab.values.at
│       │   │       │   │   │   └── sources-referenced-by-plugins.tab_i.len
│       │   │       │   │   ├── inputs/
│       │   │       │   │   │   ├── source-to-output.tab
│       │   │       │   │   │   ├── source-to-output.tab.keystream
│       │   │       │   │   │   ├── source-to-output.tab.keystream.len
│       │   │       │   │   │   ├── source-to-output.tab.len
│       │   │       │   │   │   ├── source-to-output.tab.values.at
│       │   │       │   │   │   ├── source-to-output.tab_i
│       │   │       │   │   │   └── source-to-output.tab_i.len
│       │   │       │   │   ├── jvm/
│       │   │       │   │   │   └── kotlin/
│       │   │       │   │   │       ├── class-attributes.tab
│       │   │       │   │   │       ├── class-attributes.tab.keystream
│       │   │       │   │   │       ├── class-attributes.tab.keystream.len
│       │   │       │   │   │       ├── class-attributes.tab.len
│       │   │       │   │   │       ├── class-attributes.tab.values.at
│       │   │       │   │   │       ├── class-attributes.tab_i
│       │   │       │   │   │       ├── class-attributes.tab_i.len
│       │   │       │   │   │       ├── class-fq-name-to-source.tab
│       │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream
│       │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│       │   │       │   │   │       ├── class-fq-name-to-source.tab.len
│       │   │       │   │   │       ├── class-fq-name-to-source.tab.values.at
│       │   │       │   │   │       ├── class-fq-name-to-source.tab_i
│       │   │       │   │   │       ├── class-fq-name-to-source.tab_i.len
│       │   │       │   │   │       ├── internal-name-to-source.tab
│       │   │       │   │   │       ├── internal-name-to-source.tab.keystream
│       │   │       │   │   │       ├── internal-name-to-source.tab.keystream.len
│       │   │       │   │   │       ├── internal-name-to-source.tab.len
│       │   │       │   │   │       ├── internal-name-to-source.tab.values.at
│       │   │       │   │   │       ├── internal-name-to-source.tab_i
│       │   │       │   │   │       ├── internal-name-to-source.tab_i.len
│       │   │       │   │   │       ├── package-parts.tab
│       │   │       │   │   │       ├── package-parts.tab.keystream
│       │   │       │   │   │       ├── package-parts.tab.keystream.len
│       │   │       │   │   │       ├── package-parts.tab.len
│       │   │       │   │   │       ├── package-parts.tab.values.at
│       │   │       │   │   │       ├── package-parts.tab_i
│       │   │       │   │   │       ├── package-parts.tab_i.len
│       │   │       │   │   │       ├── proto.tab
│       │   │       │   │   │       ├── proto.tab.keystream
│       │   │       │   │   │       ├── proto.tab.keystream.len
│       │   │       │   │   │       ├── proto.tab.len
│       │   │       │   │   │       ├── proto.tab.values.at
│       │   │       │   │   │       ├── proto.tab_i
│       │   │       │   │   │       ├── proto.tab_i.len
│       │   │       │   │   │       ├── source-to-classes.tab
│       │   │       │   │   │       ├── source-to-classes.tab.keystream
│       │   │       │   │   │       ├── source-to-classes.tab.keystream.len
│       │   │       │   │   │       ├── source-to-classes.tab.len
│       │   │       │   │   │       ├── source-to-classes.tab.values.at
│       │   │       │   │   │       ├── source-to-classes.tab_i
│       │   │       │   │   │       ├── source-to-classes.tab_i.len
│       │   │       │   │   │       ├── subtypes.tab
│       │   │       │   │   │       ├── subtypes.tab.keystream
│       │   │       │   │   │       ├── subtypes.tab.keystream.len
│       │   │       │   │   │       ├── subtypes.tab.len
│       │   │       │   │   │       ├── subtypes.tab.values.at
│       │   │       │   │   │       ├── subtypes.tab_i
│       │   │       │   │   │       ├── subtypes.tab_i.len
│       │   │       │   │   │       ├── supertypes.tab
│       │   │       │   │   │       ├── supertypes.tab.keystream
│       │   │       │   │   │       ├── supertypes.tab.keystream.len
│       │   │       │   │   │       ├── supertypes.tab.len
│       │   │       │   │   │       ├── supertypes.tab.values.at
│       │   │       │   │   │       ├── supertypes.tab_i
│       │   │       │   │   │       └── supertypes.tab_i.len
│       │   │       │   │   └── lookups/
│       │   │       │   │       ├── counters.tab
│       │   │       │   │       ├── file-to-id.tab
│       │   │       │   │       ├── file-to-id.tab.keystream
│       │   │       │   │       ├── file-to-id.tab.keystream.len
│       │   │       │   │       ├── file-to-id.tab.len
│       │   │       │   │       ├── file-to-id.tab.values.at
│       │   │       │   │       ├── file-to-id.tab_i
│       │   │       │   │       ├── file-to-id.tab_i.len
│       │   │       │   │       ├── id-to-file.tab
│       │   │       │   │       ├── id-to-file.tab.keystream
│       │   │       │   │       ├── id-to-file.tab.keystream.len
│       │   │       │   │       ├── id-to-file.tab.len
│       │   │       │   │       ├── id-to-file.tab.values.at
│       │   │       │   │       ├── id-to-file.tab_i
│       │   │       │   │       ├── id-to-file.tab_i.len
│       │   │       │   │       ├── lookups.tab
│       │   │       │   │       ├── lookups.tab.keystream
│       │   │       │   │       ├── lookups.tab.keystream.len
│       │   │       │   │       ├── lookups.tab.len
│       │   │       │   │       ├── lookups.tab.values.at
│       │   │       │   │       ├── lookups.tab_i
│       │   │       │   │       └── lookups.tab_i.len
│       │   │       │   └── last-build.bin
│       │   │       ├── classpath-snapshot/
│       │   │       │   └── shrunk-classpath-snapshot.bin
│       │   │       └── local-state/
│       │   ├── outputs/
│       │   │   ├── aar/
│       │   │   │   └── vault-debug.aar
│       │   │   ├── androidTest-results/connected/debug/
│       │   │   ├── apk/androidTest/debug/
│       │   │   │   ├── output-metadata.json
│       │   │   │   └── vault-debug-androidTest.apk
│       │   │   ├── code_coverage/debugAndroidTest/connected/
│       │   │   │   └── coverage.ec
│       │   │   ├── connected_android_test_additional_output/debugAndroidTest/connected/
│       │   │   └── logs/
│       │   │       ├── manifest-merger-debug-report.txt
│       │   │       └── manifest-merger-release-report.txt
│       │   └── reports/androidTests/connected/debug/
│       │       ├── css/
│       │       │   ├── base-style.css
│       │       │   └── style.css
│       │       ├── js/
│       │       │   └── report.js
│       │       └── index.html
│       ├── src/main/
│       │   ├── java/com/miguelrodriguez19/safecube/feature/vault/navigation/
│       │   │   ├── CreateVaultScreen.kt
│       │   │   ├── NavigationBar.kt
│       │   │   ├── RecoveryKeyScreen.kt
│       │   │   ├── SettingsScreen.kt
│       │   │   ├── UnlockVaultScreen.kt
│       │   │   ├── VaultFoldersScreen.kt
│       │   │   └── VaultScreen.kt
│       │   └── AndroidManifest.xml
│       └── build.gradle.kts
├── gradle/
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   │   └── libs.versions.toml
├── scripts/
│   ├── .build/com/safecube/tooling/
│   │   ├── FolderTreeToFile.class
│   │   └── Logger.class
│   ├── resources/com/safecube/tooling/
│   │   └── FolderTreeToFile.java
│   └── run-folder-tree.sh
├── .gitignore
├── build.gradle.kts
├── gradlew
├── gradlew.bat
├── LICENSE
└── settings.gradle.kts
```
