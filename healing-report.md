# 🤖 Self-Healing Execution Report
**Started at:** 2026-04-20T04:53:33.123451614

| Action / Step | Status | Original Locator | Healed Locator | Confidence |
|---|---|---|---|---|
| Setup | ✅ Pased | `By.xpath: //input[@type='email']` | - | - |
| Setup | ✅ Pased | `By.xpath: //input[@type='password']` | - | - |
| Setup | 🏥 Auto-Healed | `By.xpath: //button[@type='submit']` | `//input[@type='submit' or translate(@value, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='login' or translate(@value, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='sign in'] | //button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'login') or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sign in')] | //a[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'login') or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sign in')]` | 0.98 |
| Test Case 25 | 🏥 Auto-Healed | `By.id: logout-broken` | `//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sign out')] | //input[@type='submit' or @type='button'][contains(translate(@value, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sign out')]` | 0.90 |
